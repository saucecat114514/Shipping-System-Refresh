package com.shipping.service;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Order;
import com.shipping.model.dto.OrderRequest;
import com.shipping.model.dto.OrderQueryRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    Result<Order> createOrder(OrderRequest orderRequest);

    /**
     * 客户创建订单（只选择港口，不分配航次）
     */
    Result<Order> createCustomerOrder(OrderRequest orderRequest);

    /**
     * 查询待分配航次的订单
     */
    Result<PageResult<Order>> getPendingAssignmentOrders(OrderQueryRequest queryRequest);

    /**
     * 删除订单
     */
    Result<Void> deleteOrder(Long id);

    /**
     * 更新订单信息
     */
    Result<Order> updateOrder(Long id, OrderRequest orderRequest);

    /**
     * 根据ID查询订单
     */
    Result<Order> getOrderById(Long id);

    /**
     * 根据订单编号查询订单
     */
    Result<Order> getOrderByOrderNumber(String orderNumber);

    /**
     * 分页查询订单列表
     */
    Result<PageResult<Order>> getOrderPage(OrderQueryRequest queryRequest);

    /**
     * 分页查询订单列表（包含客户和航次信息）
     */
    Result<PageResult<Order>> getOrderPageWithDetails(OrderQueryRequest queryRequest);

    /**
     * 根据客户ID查询订单列表
     */
    Result<List<Order>> getOrdersByCustomerId(Long customerId);

    /**
     * 根据航次ID查询订单列表
     */
    Result<List<Order>> getOrdersByVoyageId(Long voyageId);

    /**
     * 更新订单状态
     */
    Result<Void> updateOrderStatus(Long id, String status);

    /**
     * 分配航次给订单
     */
    Result<Void> assignVoyageToOrder(Long orderId, Long voyageId);

    /**
     * 确认订单（分配航次并计算运价）
     */
    Result<Order> confirmOrder(Long orderId, Long voyageId);

    /**
     * 计算订单基础运价
     */
    Result<BigDecimal> calculateBasePrice(OrderRequest orderRequest, Long routeId);

    /**
     * 计算附加费用
     */
    Result<BigDecimal> calculateAdditionalFees(OrderRequest orderRequest);

    /**
     * 更新订单运价信息
     */
    Result<Void> updateOrderPricing(Long id, BigDecimal basePrice, BigDecimal additionalFees);

    /**
     * 获取客户的订单统计
     */
    Result<List<Order>> getCustomerOrders(Long customerId, String status);

    /**
     * 根据状态统计订单数量
     */
    Result<Integer> countOrdersByStatus(String status);

    /**
     * 批量更新订单状态（用于航次状态变更时）
     */
    Result<Void> batchUpdateOrderStatus(Long voyageId, String status);

    /**
     * 获取客户订单统计信息
     */
    Result<Map<String, Integer>> getCustomerOrderStats(Long customerId);
} 