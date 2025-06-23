package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import com.shipping.model.entity.Order;
import com.shipping.model.dto.OrderRequest;
import com.shipping.model.dto.OrderQueryRequest;
import com.shipping.service.OrderService;
import com.shipping.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单管理控制器
 */
@Tag(name = "订单管理", description = "订单信息的增删改查操作和运价计算")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "添加新的订单信息")
    @PostMapping
    @RequireRole({"ADMIN", "DISPATCHER", "CUSTOMER"})
    public Result<Order> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
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
     * 批量更新订单状态
     */
    @Operation(summary = "批量更新订单状态", description = "批量更新指定航次的所有订单状态")
    @PatchMapping("/batch-update-status")
    public Result<Void> batchUpdateOrderStatus(
            @Parameter(description = "航次ID") @RequestParam Long voyageId,
            @Parameter(description = "新状态") @RequestParam String status) {
        return orderService.batchUpdateOrderStatus(voyageId, status);
    }
} 