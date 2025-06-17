package com.shipping.service;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Route;
import com.shipping.model.dto.RouteRequest;
import com.shipping.model.dto.RouteQueryRequest;

import java.util.List;

/**
 * 航线服务接口
 */
public interface RouteService {

    /**
     * 创建航线
     * @param routeRequest 航线请求信息
     * @return 操作结果
     */
    Result<Route> createRoute(RouteRequest routeRequest);

    /**
     * 根据ID删除航线
     * @param id 航线ID
     * @return 操作结果
     */
    Result<Void> deleteRoute(Long id);

    /**
     * 更新航线信息
     * @param id 航线ID
     * @param routeRequest 更新的航线信息
     * @return 操作结果
     */
    Result<Route> updateRoute(Long id, RouteRequest routeRequest);

    /**
     * 根据ID查询航线
     * @param id 航线ID
     * @return 航线信息
     */
    Result<Route> getRouteById(Long id);

    /**
     * 根据航线编号查询航线
     * @param routeNumber 航线编号
     * @return 航线信息
     */
    Result<Route> getRouteByRouteNumber(String routeNumber);

    /**
     * 分页查询航线列表
     * @param queryRequest 查询条件
     * @return 分页结果
     */
    Result<PageResult<Route>> getRoutePage(RouteQueryRequest queryRequest);

    /**
     * 分页查询航线列表（包含港口信息）
     * @param queryRequest 查询条件
     * @return 分页结果
     */
    Result<PageResult<Route>> getRoutePageWithPorts(RouteQueryRequest queryRequest);

    /**
     * 获取所有航线列表
     * @return 航线列表
     */
    Result<List<Route>> getAllRoutes();
} 