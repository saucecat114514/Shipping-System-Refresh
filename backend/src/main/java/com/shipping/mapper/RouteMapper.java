package com.shipping.mapper;

import com.shipping.model.entity.Route;
import com.shipping.model.dto.RouteQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 航线Mapper接口
 */
@Mapper
public interface RouteMapper {

    /**
     * 插入航线信息
     * @param route 航线实体
     * @return 影响行数
     */
    int insert(Route route);

    /**
     * 根据ID删除航线
     * @param id 航线ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新航线信息
     * @param route 航线实体
     * @return 影响行数
     */
    int updateById(Route route);

    /**
     * 根据ID查询航线
     * @param id 航线ID
     * @return 航线实体
     */
    Route selectById(Long id);

    /**
     * 根据航线编号查询航线
     * @param routeNumber 航线编号
     * @return 航线实体
     */
    Route selectByRouteNumber(String routeNumber);

    /**
     * 分页查询航线列表
     * @param queryRequest 查询请求参数
     * @return 航线列表
     */
    List<Route> selectPageList(RouteQueryRequest queryRequest);

    /**
     * 查询航线总数
     * @param queryRequest 查询请求参数
     * @return 总数
     */
    long selectTotal(RouteQueryRequest queryRequest);

    /**
     * 查询所有航线（用于下拉框等）
     * @return 航线列表
     */
    List<Route> selectAll();

    /**
     * 检查航线编号是否存在
     * @param routeNumber 航线编号
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByRouteNumber(@Param("routeNumber") String routeNumber, @Param("excludeId") Long excludeId);

    /**
     * 查询包含港口信息的航线列表
     * @param queryRequest 查询请求参数
     * @return 航线列表（包含港口信息）
     */
    List<Route> selectPageListWithPorts(RouteQueryRequest queryRequest);
} 