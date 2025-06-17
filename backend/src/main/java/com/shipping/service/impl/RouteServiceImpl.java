package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.PortMapper;
import com.shipping.mapper.RouteMapper;
import com.shipping.model.entity.Route;
import com.shipping.model.dto.RouteRequest;
import com.shipping.model.dto.RouteQueryRequest;
import com.shipping.service.RouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 航线服务实现类
 */
@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private PortMapper portMapper;

    @Override
    public Result<Route> createRoute(RouteRequest routeRequest) {
        // 检查航线编号是否已存在
        if (routeMapper.existsByRouteNumber(routeRequest.getRouteNumber(), null)) {
            throw new BusinessException("航线编号已存在：" + routeRequest.getRouteNumber());
        }

        // 检查起始港口是否存在
        if (portMapper.selectById(routeRequest.getOriginPortId()) == null) {
            throw new BusinessException("起始港口不存在，ID：" + routeRequest.getOriginPortId());
        }

        // 检查目的港口是否存在
        if (portMapper.selectById(routeRequest.getDestinationPortId()) == null) {
            throw new BusinessException("目的港口不存在，ID：" + routeRequest.getDestinationPortId());
        }

        // 检查起始港口和目的港口不能相同
        if (routeRequest.getOriginPortId().equals(routeRequest.getDestinationPortId())) {
            throw new BusinessException("起始港口和目的港口不能相同");
        }

        // 创建航线实体
        Route route = new Route();
        BeanUtils.copyProperties(routeRequest, route);
        
        // 计算海里距离（1公里 = 0.539957海里）
        if (routeRequest.getDistance() != null) {
            BigDecimal distanceNm = routeRequest.getDistance().multiply(new BigDecimal("0.539957"));
            route.setDistanceNm(distanceNm.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());

        // 插入数据库
        int result = routeMapper.insert(route);
        if (result > 0) {
            return Result.success(route);
        } else {
            throw new BusinessException("创建航线失败");
        }
    }

    @Override
    public Result<Void> deleteRoute(Long id) {
        // 检查航线是否存在
        Route existingRoute = routeMapper.selectById(id);
        if (existingRoute == null) {
            throw new BusinessException("航线不存在，ID：" + id);
        }

        // TODO: 检查航线是否被航次使用，如果被使用则不能删除
        // 可以在这里添加检查逻辑

        // 删除航线
        int result = routeMapper.deleteById(id);
        if (result > 0) {
            return Result.success();
        } else {
            throw new BusinessException("删除航线失败");
        }
    }

    @Override
    public Result<Route> updateRoute(Long id, RouteRequest routeRequest) {
        // 检查航线是否存在
        Route existingRoute = routeMapper.selectById(id);
        if (existingRoute == null) {
            throw new BusinessException("航线不存在，ID：" + id);
        }

        // 检查航线编号是否被其他航线使用
        if (routeMapper.existsByRouteNumber(routeRequest.getRouteNumber(), id)) {
            throw new BusinessException("航线编号已存在：" + routeRequest.getRouteNumber());
        }

        // 检查起始港口是否存在
        if (portMapper.selectById(routeRequest.getOriginPortId()) == null) {
            throw new BusinessException("起始港口不存在，ID：" + routeRequest.getOriginPortId());
        }

        // 检查目的港口是否存在
        if (portMapper.selectById(routeRequest.getDestinationPortId()) == null) {
            throw new BusinessException("目的港口不存在，ID：" + routeRequest.getDestinationPortId());
        }

        // 检查起始港口和目的港口不能相同
        if (routeRequest.getOriginPortId().equals(routeRequest.getDestinationPortId())) {
            throw new BusinessException("起始港口和目的港口不能相同");
        }

        // 更新航线信息
        Route updateRoute = new Route();
        BeanUtils.copyProperties(routeRequest, updateRoute);
        updateRoute.setId(id);
        
        // 计算海里距离
        if (routeRequest.getDistance() != null) {
            BigDecimal distanceNm = routeRequest.getDistance().multiply(new BigDecimal("0.539957"));
            updateRoute.setDistanceNm(distanceNm.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        updateRoute.setCreatedAt(existingRoute.getCreatedAt());
        updateRoute.setUpdatedAt(LocalDateTime.now());

        int result = routeMapper.updateById(updateRoute);
        if (result > 0) {
            return Result.success(routeMapper.selectById(id));
        } else {
            throw new BusinessException("更新航线失败");
        }
    }

    @Override
    public Result<Route> getRouteById(Long id) {
        Route route = routeMapper.selectById(id);
        if (route == null) {
            throw new BusinessException("航线不存在，ID：" + id);
        }
        return Result.success(route);
    }

    @Override
    public Result<Route> getRouteByRouteNumber(String routeNumber) {
        Route route = routeMapper.selectByRouteNumber(routeNumber);
        if (route == null) {
            throw new BusinessException("航线不存在，编号：" + routeNumber);
        }
        return Result.success(route);
    }

    @Override
    public Result<PageResult<Route>> getRoutePage(RouteQueryRequest queryRequest) {
        // 参数校验和默认值设置
        if (queryRequest.getPage() < 1) {
            queryRequest.setPage(1);
        }
        if (queryRequest.getSize() < 1) {
            queryRequest.setSize(10);
        }
        if (queryRequest.getSize() > 100) {
            queryRequest.setSize(100);
        }

        // 查询数据和总数
        List<Route> routes = routeMapper.selectPageList(queryRequest);
        long total = routeMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Route> pageResult = new PageResult<>();
        pageResult.setRecords(routes);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<Route>> getRoutePageWithPorts(RouteQueryRequest queryRequest) {
        // 参数校验和默认值设置
        if (queryRequest.getPage() < 1) {
            queryRequest.setPage(1);
        }
        if (queryRequest.getSize() < 1) {
            queryRequest.setSize(10);
        }
        if (queryRequest.getSize() > 100) {
            queryRequest.setSize(100);
        }

        // 查询数据和总数
        List<Route> routes = routeMapper.selectPageListWithPorts(queryRequest);
        long total = routeMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Route> pageResult = new PageResult<>();
        pageResult.setRecords(routes);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<List<Route>> getAllRoutes() {
        List<Route> routes = routeMapper.selectAll();
        return Result.success(routes);
    }
} 