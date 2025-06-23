package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.OrderMapper;
import com.shipping.mapper.UserMapper;
import com.shipping.mapper.VoyageMapper;
import com.shipping.mapper.RouteMapper;
import com.shipping.model.entity.Order;
import com.shipping.model.entity.User;
import com.shipping.model.entity.Voyage;
import com.shipping.model.entity.Route;
import com.shipping.model.dto.OrderRequest;
import com.shipping.model.dto.OrderQueryRequest;
import com.shipping.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VoyageMapper voyageMapper;

    @Autowired
    private RouteMapper routeMapper;

    // 运价计算基础费率（元/吨/公里）
    private static final BigDecimal BASE_RATE = new BigDecimal("0.5");

    @Override
    public Result<Order> createOrder(OrderRequest orderRequest) {
        logger.info("创建订单: {}", orderRequest.getOrderNumber());

        // 检查订单编号是否已存在
        if (orderMapper.existsByOrderNumber(orderRequest.getOrderNumber(), null)) {
            throw new BusinessException("订单编号已存在：" + orderRequest.getOrderNumber());
        }

        // 检查客户ID是否提供
        if (orderRequest.getCustomerId() == null) {
            throw new BusinessException("客户ID不能为空");
        }

        // 检查客户是否存在
        User customer = userMapper.findById(orderRequest.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在，ID：" + orderRequest.getCustomerId());
        }

        // 如果指定了航次，检查航次是否存在
        if (orderRequest.getVoyageId() != null) {
            Voyage voyage = voyageMapper.selectById(orderRequest.getVoyageId());
            if (voyage == null) {
                throw new BusinessException("航次不存在，ID：" + orderRequest.getVoyageId());
            }
        }

        // 创建订单实体
        Order order = new Order();
        BeanUtils.copyProperties(orderRequest, order);
        
        // 设置默认状态和值
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        if (order.getIsUrgent() == null) {
            order.setIsUrgent(false);
        }
        if (order.getAdditionalFees() == null) {
            order.setAdditionalFees(BigDecimal.ZERO);
        }
        
        // 计算并设置基础价格
        if (order.getBasePrice() == null) {
            BigDecimal basePrice = calculateBasePriceInternal(orderRequest, orderRequest.getRouteId());
            order.setBasePrice(basePrice);
        }
        
        // 计算并设置总价格
        if (order.getTotalPrice() == null) {
            BigDecimal totalPrice = order.getBasePrice().add(order.getAdditionalFees());
            order.setTotalPrice(totalPrice);
        }
        
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 插入数据库
        int result = orderMapper.insert(order);
        if (result > 0) {
            logger.info("订单创建成功，ID: {}", order.getId());
            return Result.success(order);
        } else {
            throw new BusinessException("创建订单失败");
        }
    }

    @Override
    public Result<Void> deleteOrder(Long id) {
        logger.info("删除订单，ID: {}", id);

        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在，ID：" + id);
        }

        if ("IN_TRANSIT".equals(existingOrder.getStatus()) || "DELIVERED".equals(existingOrder.getStatus())) {
            throw new BusinessException("订单已发货或已送达，无法删除");
        }

        int result = orderMapper.deleteById(id);
        if (result > 0) {
            logger.info("订单删除成功，ID: {}", id);
            return Result.success();
        } else {
            throw new BusinessException("删除订单失败");
        }
    }

    @Override
    public Result<Order> updateOrder(Long id, OrderRequest orderRequest) {
        logger.info("更新订单，ID: {}", id);

        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在，ID：" + id);
        }

        if ("IN_TRANSIT".equals(existingOrder.getStatus()) || "DELIVERED".equals(existingOrder.getStatus())) {
            throw new BusinessException("订单已发货或已送达，无法修改");
        }

        if (orderMapper.existsByOrderNumber(orderRequest.getOrderNumber(), id)) {
            throw new BusinessException("订单编号已存在：" + orderRequest.getOrderNumber());
        }

        if (userMapper.findById(orderRequest.getCustomerId()) == null) {
            throw new BusinessException("客户不存在，ID：" + orderRequest.getCustomerId());
        }

        if (orderRequest.getVoyageId() != null) {
            if (voyageMapper.selectById(orderRequest.getVoyageId()) == null) {
                throw new BusinessException("航次不存在，ID：" + orderRequest.getVoyageId());
            }
        }

        Order updateOrder = new Order();
        BeanUtils.copyProperties(orderRequest, updateOrder);
        updateOrder.setId(id);
        updateOrder.setCreatedAt(existingOrder.getCreatedAt());
        updateOrder.setUpdatedAt(LocalDateTime.now());

        int result = orderMapper.updateById(updateOrder);
        if (result > 0) {
            logger.info("订单更新成功，ID: {}", id);
            return Result.success(orderMapper.selectById(id));
        } else {
            throw new BusinessException("更新订单失败");
        }
    }

    @Override
    public Result<Order> getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在，ID：" + id);
        }
        return Result.success(order);
    }

    @Override
    public Result<Order> getOrderByOrderNumber(String orderNumber) {
        Order order = orderMapper.selectByOrderNumber(orderNumber);
        if (order == null) {
            throw new BusinessException("订单不存在，编号：" + orderNumber);
        }
        return Result.success(order);
    }

    @Override
    public Result<PageResult<Order>> getOrderPage(OrderQueryRequest queryRequest) {
        if (queryRequest.getPage() < 1) queryRequest.setPage(1);
        if (queryRequest.getSize() < 1) queryRequest.setSize(10);
        if (queryRequest.getSize() > 100) queryRequest.setSize(100);

        List<Order> orders = orderMapper.selectPageList(queryRequest);
        long total = orderMapper.selectTotal(queryRequest);

        PageResult<Order> pageResult = new PageResult<>();
        pageResult.setRecords(orders);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<Order>> getOrderPageWithDetails(OrderQueryRequest queryRequest) {
        if (queryRequest.getPage() < 1) queryRequest.setPage(1);
        if (queryRequest.getSize() < 1) queryRequest.setSize(10);
        if (queryRequest.getSize() > 100) queryRequest.setSize(100);

        List<Order> orders = orderMapper.selectPageListWithDetails(queryRequest);
        long total = orderMapper.selectTotal(queryRequest);

        PageResult<Order> pageResult = new PageResult<>();
        pageResult.setRecords(orders);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<List<Order>> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderMapper.selectByCustomerId(customerId);
        return Result.success(orders);
    }

    @Override
    public Result<List<Order>> getOrdersByVoyageId(Long voyageId) {
        List<Order> orders = orderMapper.selectByVoyageId(voyageId);
        return Result.success(orders);
    }

    @Override
    public Result<Void> updateOrderStatus(Long id, String status) {
        Order existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            throw new BusinessException("订单不存在，ID：" + id);
        }

        if (!isValidStatus(status)) {
            throw new BusinessException("无效的订单状态：" + status);
        }

        int result = orderMapper.updateStatus(id, status);
        if (result > 0) {
            logger.info("订单状态更新成功，ID: {}, 状态: {}", id, status);
            return Result.success();
        } else {
            throw new BusinessException("更新订单状态失败");
        }
    }

    @Override
    public Result<Void> assignVoyageToOrder(Long orderId, Long voyageId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在，ID：" + orderId);
        }

        Voyage voyage = voyageMapper.selectById(voyageId);
        if (voyage == null) {
            throw new BusinessException("航次不存在，ID：" + voyageId);
        }

        int result = orderMapper.assignVoyage(orderId, voyageId);
        if (result > 0) {
            logger.info("航次分配成功，订单ID: {}, 航次ID: {}", orderId, voyageId);
            return Result.success();
        } else {
            throw new BusinessException("分配航次失败");
        }
    }

    @Override
    public Result<Order> confirmOrder(Long orderId, Long voyageId) {
        logger.info("确认订单，订单ID: {}, 航次ID: {}", orderId, voyageId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在，ID：" + orderId);
        }

        Voyage voyage = voyageMapper.selectById(voyageId);
        if (voyage == null) {
            throw new BusinessException("航次不存在，ID：" + voyageId);
        }

        Route route = routeMapper.selectById(voyage.getRouteId());
        if (route == null) {
            throw new BusinessException("航线不存在，ID：" + voyage.getRouteId());
        }

        // 分配航次
        orderMapper.assignVoyage(orderId, voyageId);

        // 计算运价
        OrderRequest orderRequest = new OrderRequest();
        BeanUtils.copyProperties(order, orderRequest);
        
        BigDecimal basePrice = calculateBasePriceInternal(orderRequest, route.getId());
        BigDecimal additionalFees = calculateAdditionalFeesInternal(orderRequest);
        BigDecimal totalPrice = basePrice.add(additionalFees);

        // 更新运价信息
        orderMapper.updatePricing(orderId, basePrice, additionalFees, totalPrice);

        // 更新状态为已确认
        orderMapper.updateStatus(orderId, "CONFIRMED");

        logger.info("订单确认成功，订单ID: {}, 总价: {}", orderId, totalPrice);
        return Result.success(orderMapper.selectById(orderId));
    }

    @Override
    public Result<BigDecimal> calculateBasePrice(OrderRequest orderRequest, Long routeId) {
        BigDecimal basePrice = calculateBasePriceInternal(orderRequest, routeId);
        return Result.success(basePrice);
    }

    @Override
    public Result<BigDecimal> calculateAdditionalFees(OrderRequest orderRequest) {
        BigDecimal additionalFees = calculateAdditionalFeesInternal(orderRequest);
        return Result.success(additionalFees);
    }

    @Override
    public Result<Void> updateOrderPricing(Long id, BigDecimal basePrice, BigDecimal additionalFees) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在，ID：" + id);
        }

        BigDecimal totalPrice = basePrice.add(additionalFees);
        int result = orderMapper.updatePricing(id, basePrice, additionalFees, totalPrice);
        if (result > 0) {
            logger.info("订单运价更新成功，ID: {}, 总价: {}", id, totalPrice);
            return Result.success();
        } else {
            throw new BusinessException("更新订单运价失败");
        }
    }

    @Override
    public Result<List<Order>> getCustomerOrders(Long customerId, String status) {
        List<Order> orders = orderMapper.selectCustomerOrders(customerId, status);
        return Result.success(orders);
    }

    @Override
    public Result<Integer> countOrdersByStatus(String status) {
        int count = orderMapper.countByStatus(status);
        return Result.success(count);
    }

    @Override
    public Result<Void> batchUpdateOrderStatus(Long voyageId, String status) {
        // 获取该航次的所有订单并批量更新状态
        List<Order> orders = orderMapper.selectByVoyageId(voyageId);
        for (Order order : orders) {
            orderMapper.updateStatus(order.getId(), status);
        }
        logger.info("批量更新订单状态完成，航次ID: {}, 状态: {}, 影响订单数: {}", voyageId, status, orders.size());
        return Result.success();
    }

    /**
     * 计算基础运价
     */
    private BigDecimal calculateBasePriceInternal(OrderRequest orderRequest, Long routeId) {
        BigDecimal distance;
        
        if (routeId != null) {
            Route route = routeMapper.selectById(routeId);
            if (route == null) {
                throw new BusinessException("航线不存在，ID：" + routeId);
            }
            // 使用海里距离而不是公里距离
            distance = route.getDistanceNm();
            if (distance == null) {
                // 如果海里距离为空，从公里距离计算
                BigDecimal distanceKm = route.getDistance();
                if (distanceKm != null) {
                    distance = distanceKm.divide(new BigDecimal("1.852"), 2, BigDecimal.ROUND_HALF_UP);
                } else {
                    distance = new BigDecimal("500");
                }
            }
        } else {
            // 如果没有指定路线，使用默认距离（500海里）
            distance = new BigDecimal("500");
        }

        // 基础运价 = 货物重量 × 航程距离（海里） × 基础费率
        BigDecimal basePrice = orderRequest.getCargoWeight()
                .multiply(distance)
                .multiply(BASE_RATE);

        // 加急订单费率上浮20%
        if (Boolean.TRUE.equals(orderRequest.getIsUrgent())) {
            basePrice = basePrice.multiply(new BigDecimal("1.2"));
        }

        return basePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算附加费用
     */
    private BigDecimal calculateAdditionalFeesInternal(OrderRequest orderRequest) {
        BigDecimal additionalFees = BigDecimal.ZERO;

        // 根据货物类型计算附加费
        String cargoType = orderRequest.getCargoType();
        if ("危险品".equals(cargoType)) {
            // 危险品附加费：基础运价的30%
            if (orderRequest.getBasePrice() != null) {
                additionalFees = additionalFees.add(orderRequest.getBasePrice().multiply(new BigDecimal("0.3")));
            } else {
                // 如果没有基础运价，按重量计算固定费用
                additionalFees = additionalFees.add(orderRequest.getCargoWeight().multiply(new BigDecimal("100")));
            }
        } else if ("冷藏货物".equals(cargoType)) {
            // 冷藏货物附加费：每吨50元
            additionalFees = additionalFees.add(orderRequest.getCargoWeight().multiply(new BigDecimal("50")));
        } else if ("液体货物".equals(cargoType)) {
            // 液体货物附加费：每吨30元
            additionalFees = additionalFees.add(orderRequest.getCargoWeight().multiply(new BigDecimal("30")));
        }

        return additionalFees.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 验证状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) || "CONFIRMED".equals(status) || 
               "IN_TRANSIT".equals(status) || "DELIVERED".equals(status) || 
               "CANCELLED".equals(status);
    }
} 