package com.shipping.mapper;

import com.shipping.model.entity.Order;
import com.shipping.model.dto.OrderQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     */
    int insert(Order order);

    /**
     * 根据ID删除订单
     */
    int deleteById(Long id);

    /**
     * 根据ID更新订单
     */
    int updateById(Order order);

    /**
     * 根据ID查询订单
     */
    Order selectById(Long id);

    /**
     * 根据订单编号查询订单
     */
    Order selectByOrderNumber(String orderNumber);

    /**
     * 检查订单编号是否存在（排除指定ID）
     */
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber, @Param("excludeId") Long excludeId);

    /**
     * 分页查询订单列表
     */
    List<Order> selectPageList(OrderQueryRequest queryRequest);

    /**
     * 分页查询订单列表（包含客户和航次信息）
     */
    List<Order> selectPageListWithDetails(OrderQueryRequest queryRequest);

    /**
     * 查询总数
     */
    long selectTotal(OrderQueryRequest queryRequest);

    /**
     * 根据客户ID查询订单列表
     */
    List<Order> selectByCustomerId(Long customerId);

    /**
     * 根据航次ID查询订单列表
     */
    List<Order> selectByVoyageId(Long voyageId);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 分配航次给订单
     */
    int assignVoyage(@Param("id") Long id, @Param("voyageId") Long voyageId);

    /**
     * 更新订单运价信息
     */
    int updatePricing(@Param("id") Long id, @Param("basePrice") java.math.BigDecimal basePrice, 
                     @Param("additionalFees") java.math.BigDecimal additionalFees, 
                     @Param("totalPrice") java.math.BigDecimal totalPrice);

    /**
     * 根据状态统计订单数量
     */
    int countByStatus(String status);

    /**
     * 获取客户的所有订单
     */
    List<Order> selectCustomerOrders(@Param("customerId") Long customerId, @Param("status") String status);
} 