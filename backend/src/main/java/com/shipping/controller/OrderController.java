package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import com.shipping.exception.BusinessException;
import com.shipping.model.entity.Order;
import com.shipping.model.entity.User;
import com.shipping.model.dto.OrderRequest;
import com.shipping.model.dto.OrderQueryRequest;
import com.shipping.model.dto.OrderPriceInfo;
import com.shipping.service.OrderService;
import com.shipping.service.UserService;
import com.shipping.service.VoyageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单管理控制器
 */
@Tag(name = "订单管理", description = "订单信息的增删改查操作和运价计算")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private VoyageService voyageService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "添加新的订单信息")
    @PostMapping
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<Order> createOrder(@Valid @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        // 获取当前用户信息
        String currentUsername = (String) request.getAttribute("currentUser");
        String currentUserRole = (String) request.getAttribute("currentUserRole");
        
        logger.info("创建订单请求 - 用户: {}, 角色: {}, 原始customerId: {}", 
                   currentUsername, currentUserRole, orderRequest.getCustomerId());
        
        // 如果是客户角色，自动设置客户ID为当前用户
        if ("CUSTOMER".equals(currentUserRole) && currentUsername != null) {
            User currentUser = userService.getUserByUsername(currentUsername);
            if (currentUser != null) {
                logger.info("自动设置customerId从 {} 到 {}", orderRequest.getCustomerId(), currentUser.getId());
                orderRequest.setCustomerId(currentUser.getId());
            }
        }

        // 如果选择了航次，将航次ID设置到订单的voyageId字段
        if (orderRequest.getSelectedVoyageId() != null) {
            logger.info("设置订单航次ID: {}", orderRequest.getSelectedVoyageId());
            orderRequest.setVoyageId(orderRequest.getSelectedVoyageId());
        }
        
        logger.info("最终customerId: {}, voyageId: {}", orderRequest.getCustomerId(), orderRequest.getVoyageId());
        
        return orderService.createOrder(orderRequest);
    }

    /**
     * 客户创建订单（只选择港口，不选择航次）
     */
    @Operation(summary = "客户创建订单", description = "客户创建订单，只需选择出发港口和目的港口，航次由管理员分配")
    @PostMapping("/customer-create")
    @RequireRole({"CUSTOMER"})
    public Result<Order> createCustomerOrder(@Valid @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        // 获取当前用户信息
        String currentUsername = (String) request.getAttribute("currentUser");
        
        logger.info("客户创建订单请求 - 用户: {}", currentUsername);
        
        // 自动设置客户ID为当前用户
        if (currentUsername != null) {
            User currentUser = userService.getUserByUsername(currentUsername);
            if (currentUser != null) {
                orderRequest.setCustomerId(currentUser.getId());
            }
        }

        // 客户创建的订单不允许直接指定航次
        orderRequest.setVoyageId(null);
        orderRequest.setSelectedVoyageId(null);
        
        // 验证港口选择
        if (orderRequest.getOriginPortId() == null || orderRequest.getDestinationPortId() == null) {
            throw new BusinessException("请选择出发港口和目的港口");
        }
        
        return orderService.createCustomerOrder(orderRequest);
    }

    /**
     * 查询待分配航次的订单（管理员使用）
     */
    @Operation(summary = "查询待分配航次订单", description = "查询等待分配航次的订单列表，供管理员进行航次分配")
    @GetMapping("/pending-assignment")
    @RequireRole({"ADMIN", "DISPATCHER"})
    public Result<PageResult<Order>> getPendingAssignmentOrders(OrderQueryRequest queryRequest) {
        return orderService.getPendingAssignmentOrders(queryRequest);
    }

    /**
     * 删除订单
     */
    @Operation(summary = "删除订单", description = "根据ID删除订单")
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "DISPATCHER"})
    public Result<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return orderService.deleteOrder(id);
    }

    /**
     * 更新订单信息
     */
    @Operation(summary = "更新订单", description = "更新订单信息")
    @PutMapping("/{id}")
    public Result<Order> updateOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody OrderRequest orderRequest) {
        return orderService.updateOrder(id, orderRequest);
    }

    /**
     * 根据ID查询订单
     */
    @Operation(summary = "查询订单详情", description = "根据ID查询订单详细信息")
    @GetMapping("/{id}")
    public Result<Order> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /**
     * 根据订单编号查询订单
     */
    @Operation(summary = "根据编号查询订单", description = "根据订单编号查询订单信息")
    @GetMapping("/number/{orderNumber}")
    public Result<Order> getOrderByOrderNumber(
            @Parameter(description = "订单编号") @PathVariable String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }

    /**
     * 分页查询订单列表
     */
    @Operation(summary = "分页查询订单", description = "分页查询订单列表，支持按编号、客户、状态等筛选")
    @GetMapping
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<PageResult<Order>> getOrderPage(
            OrderQueryRequest queryRequest,
            HttpServletRequest request) {
        
        // 如果是客户角色，只能查看自己的订单
        String userRole = (String) request.getAttribute("currentUserRole");
        String username = (String) request.getAttribute("currentUser");
        
        if ("CUSTOMER".equals(userRole)) {
            // 客户只能查看自己的订单，根据username查找用户ID
            try {
                var user = userService.getUserByUsername(username);
                if (user != null) {
                    queryRequest.setCustomerId(user.getId());
                }
            } catch (Exception e) {
                // 如果找不到用户，设置一个不存在的ID，确保不返回任何订单
                queryRequest.setCustomerId(-1L);
            }
        }
        
        return orderService.getOrderPageWithDetails(queryRequest);
    }

    /**
     * 分页查询订单列表（包含关联信息）
     */
    @Operation(summary = "分页查询订单（含关联信息）", description = "分页查询订单列表，包含关联的客户和航次详细信息")
    @GetMapping("/with-details")
    public Result<PageResult<Order>> getOrderPageWithDetails(OrderQueryRequest queryRequest) {
        return orderService.getOrderPageWithDetails(queryRequest);
    }

    /**
     * 根据客户ID查询订单
     */
    @Operation(summary = "根据客户查询订单", description = "根据客户ID查询该客户的所有订单")
    @GetMapping("/customer/{customerId}")
    public Result<List<Order>> getOrdersByCustomerId(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    /**
     * 根据航次ID查询订单
     */
    @Operation(summary = "根据航次查询订单", description = "根据航次ID查询该航次的所有订单")
    @GetMapping("/voyage/{voyageId}")
    public Result<List<Order>> getOrdersByVoyageId(
            @Parameter(description = "航次ID") @PathVariable Long voyageId) {
        return orderService.getOrdersByVoyageId(voyageId);
    }

    /**
     * 更新订单状态
     */
    @Operation(summary = "更新订单状态", description = "更新订单的状态（PENDING/CONFIRMED/IN_TRANSIT/DELIVERED/CANCELLED）")
    @PatchMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    /**
     * 分配航次给订单
     */
    @Operation(summary = "分配航次", description = "为订单分配航次")
    @PatchMapping("/{orderId}/assign-voyage")
    public Result<Void> assignVoyageToOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "航次ID") @RequestParam Long voyageId) {
        return orderService.assignVoyageToOrder(orderId, voyageId);
    }

    /**
     * 确认订单
     */
    @Operation(summary = "确认订单", description = "分配航次并计算运价，确认订单")
    @PostMapping("/{orderId}/confirm")
    public Result<Order> confirmOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "航次ID") @RequestParam Long voyageId) {
        return orderService.confirmOrder(orderId, voyageId);
    }

    /**
     * 计算订单总价格
     */
    @Operation(summary = "计算订单价格", description = "根据订单信息计算基础运价、附加费用和总价格")
    @PostMapping("/calculate-price")
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<OrderPriceInfo> calculateOrderPrice(@RequestBody OrderRequest orderRequest) {
        logger.info("价格计算请求 - 货物重量: {}, 货物类型: {}, 选择航次ID: {}, 是否加急: {}", 
                   orderRequest.getCargoWeight(), orderRequest.getCargoType(), 
                   orderRequest.getSelectedVoyageId(), orderRequest.getIsUrgent());
        
        try {
            Long routeId = null;
            
            // 如果选择了航次，从航次获取航线ID
            if (orderRequest.getSelectedVoyageId() != null) {
                var voyageResult = voyageService.getVoyageById(orderRequest.getSelectedVoyageId());
                if (voyageResult.getCode() == 200 && voyageResult.getData() != null) {
                    var voyage = voyageResult.getData();
                    routeId = voyage.getRouteId();
                    logger.info("从航次 {} 获取到航线ID: {}", orderRequest.getSelectedVoyageId(), routeId);
                } else {
                    logger.warn("找不到航次: {}", orderRequest.getSelectedVoyageId());
                }
            }
            
            // 如果没有从航次获取到航线，使用直接传入的routeId（向后兼容）
            if (routeId == null) {
                routeId = orderRequest.getRouteId();
            }
            
            // 计算基础运价
            Result<BigDecimal> basePriceResult = orderService.calculateBasePrice(orderRequest, routeId);
            BigDecimal basePrice = basePriceResult.getData();
            logger.info("基础运价计算结果: {}", basePrice);
            
            // 计算附加费用
            Result<BigDecimal> additionalFeesResult = orderService.calculateAdditionalFees(orderRequest);
            BigDecimal additionalFees = additionalFeesResult.getData();
            logger.info("附加费用计算结果: {}", additionalFees);
            
            // 计算总价格
            BigDecimal totalPrice = basePrice.add(additionalFees);
            logger.info("总价格: {}", totalPrice);
            
            // 构造返回对象
            OrderPriceInfo priceInfo = new OrderPriceInfo();
            priceInfo.setBasePrice(basePrice);
            priceInfo.setAdditionalFees(additionalFees);
            priceInfo.setTotalPrice(totalPrice);
            
            logger.info("价格计算成功");
            return Result.success(priceInfo);
        } catch (Exception e) {
            logger.error("价格计算失败", e);
            throw e;
        }
    }

    /**
     * 计算基础运价
     */
    @Operation(summary = "计算基础运价", description = "根据订单信息和航线计算基础运价")
    @PostMapping("/calculate-base-price")
    public Result<BigDecimal> calculateBasePrice(
            @Valid @RequestBody OrderRequest orderRequest,
            @Parameter(description = "航线ID") @RequestParam Long routeId) {
        return orderService.calculateBasePrice(orderRequest, routeId);
    }

    /**
     * 计算附加费用
     */
    @Operation(summary = "计算附加费用", description = "根据订单信息计算附加费用")
    @PostMapping("/calculate-additional-fees")
    public Result<BigDecimal> calculateAdditionalFees(
            @Valid @RequestBody OrderRequest orderRequest) {
        return orderService.calculateAdditionalFees(orderRequest);
    }

    /**
     * 更新订单运价
     */
    @Operation(summary = "更新订单运价", description = "手动更新订单的运价信息")
    @PatchMapping("/{id}/pricing")
    public Result<Void> updateOrderPricing(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "基础运价") @RequestParam BigDecimal basePrice,
            @Parameter(description = "附加费用") @RequestParam BigDecimal additionalFees) {
        return orderService.updateOrderPricing(id, basePrice, additionalFees);
    }

    /**
     * 获取客户订单统计
     */
    @Operation(summary = "获取客户订单", description = "获取指定客户的订单列表，可按状态筛选")
    @GetMapping("/customer/{customerId}/orders")
    public Result<List<Order>> getCustomerOrders(
            @Parameter(description = "客户ID") @PathVariable Long customerId,
            @Parameter(description = "订单状态（可选）") @RequestParam(required = false) String status) {
        return orderService.getCustomerOrders(customerId, status);
    }

    /**
     * 订单状态统计
     */
    @Operation(summary = "订单状态统计", description = "统计指定状态的订单数量")
    @GetMapping("/count-by-status")
    public Result<Integer> countOrdersByStatus(
            @Parameter(description = "订单状态") @RequestParam String status) {
        return orderService.countOrdersByStatus(status);
    }

    /**
     * 获取客户订单统计信息
     */
    @Operation(summary = "客户订单统计", description = "获取客户的各状态订单统计数据")
    @GetMapping("/customer/{customerId}/stats")
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<Map<String, Integer>> getCustomerOrderStats(
            @Parameter(description = "客户ID") @PathVariable Long customerId,
            HttpServletRequest request) {
        
        // 如果是客户角色，只能查看自己的统计
        String userRole = (String) request.getAttribute("currentUserRole");
        String username = (String) request.getAttribute("currentUser");
        
        if ("CUSTOMER".equals(userRole)) {
            try {
                var user = userService.getUserByUsername(username);
                if (user == null || !user.getId().equals(customerId)) {
                    throw new BusinessException("您只能查看自己的订单统计");
                }
            } catch (Exception e) {
                throw new BusinessException("用户验证失败");
            }
        }
        
        return orderService.getCustomerOrderStats(customerId);
    }

    /**
     * 批量更新订单状态
     */
    @Operation(summary = "批量更新订单状态", description = "批量更新指定航次的所有订单状态")
    @PatchMapping("/batch-update-status")
    public Result<Void> batchUpdateOrderStatus(
            @Parameter(description = "航次ID") @RequestParam Long voyageId,
            @Parameter(description = "新状态") @RequestParam String status) {
        return orderService.batchUpdateOrderStatus(voyageId, status);
    }

    /**
     * 取消订单（客户端专用）
     */
    @Operation(summary = "取消订单", description = "客户取消自己的订单，只有待处理和已确认状态的订单可以取消")
    @PutMapping("/{id}/cancel")
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            HttpServletRequest request) {
        
        // 获取当前用户信息
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");
        
        // 检查订单是否存在
        Result<Order> orderResult = orderService.getOrderById(id);
        if (orderResult.getCode() != 200 || orderResult.getData() == null) {
            throw new BusinessException("订单不存在");
        }
        
        Order order = orderResult.getData();
        
        // 如果是客户角色，需要验证订单归属
        if ("CUSTOMER".equals(role)) {
            try {
                var userResult = userService.getUserByUsername(username);
                if (userResult == null || !userResult.getId().equals(order.getCustomerId())) {
                    throw new BusinessException("无权操作此订单");
                }
            } catch (Exception e) {
                throw new BusinessException("用户验证失败");
            }
        }
        
        // 检查订单状态是否允许取消
        if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw new BusinessException("当前状态的订单不允许取消");
        }
        
        // 更新订单状态为已取消
        return orderService.updateOrderStatus(id, "CANCELLED");
    }
} 