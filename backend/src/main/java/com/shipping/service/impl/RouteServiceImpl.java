package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.PortMapper;
import com.shipping.mapper.RouteMapper;
import com.shipping.model.entity.Route;
import com.shipping.model.entity.Port;
import com.shipping.model.dto.RouteRequest;
import com.shipping.model.dto.RouteQueryRequest;
import com.shipping.service.RouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // 检查起始港口是否存在
        Port originPort = portMapper.selectById(routeRequest.getOriginPortId());
        if (originPort == null) {
            throw new BusinessException("起始港口不存在，ID：" + routeRequest.getOriginPortId());
        }

        // 检查目的港口是否存在
        Port destinationPort = portMapper.selectById(routeRequest.getDestinationPortId());
        if (destinationPort == null) {
            throw new BusinessException("目的港口不存在，ID：" + routeRequest.getDestinationPortId());
        }

        // 检查起始港口和目的港口不能相同
        if (routeRequest.getOriginPortId().equals(routeRequest.getDestinationPortId())) {
            throw new BusinessException("起始港口和目的港口不能相同");
        }

        // 自动生成航线编号（如果未提供）
        String routeNumber = routeRequest.getRouteNumber();
        if (!StringUtils.hasText(routeNumber)) {
            routeNumber = generateRouteNumber(originPort, destinationPort);
        }

        // 检查航线编号是否已存在
        if (routeMapper.existsByRouteNumber(routeNumber, null)) {
            throw new BusinessException("航线编号已存在：" + routeNumber);
        }

        // 自动计算距离（如果未提供）
        BigDecimal distance = routeRequest.getDistance();
        if (distance == null) {
            distance = calculateDistance(originPort, destinationPort);
        }

        // 自动计算预计航行时间（如果未提供）
        BigDecimal estimatedDuration = routeRequest.getEstimatedDuration();
        if (estimatedDuration == null) {
            estimatedDuration = calculateEstimatedDuration(distance);
        }

        // 创建航线实体
        Route route = new Route();
        BeanUtils.copyProperties(routeRequest, route);
        route.setRouteNumber(routeNumber);
        route.setDistance(distance);
        route.setEstimatedDuration(estimatedDuration);
        
        // 计算海里距离（1公里 = 0.539957海里）
        BigDecimal distanceNm = distance.multiply(new BigDecimal("0.539957"));
        route.setDistanceNm(distanceNm.setScale(2, RoundingMode.HALF_UP));
        
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

    /**
     * 自动生成航线编号
     */
    private String generateRouteNumber(Port originPort, Port destinationPort) {
        // 使用港口代码生成航线编号，格式：R + 起始港口代码前3位 + 目的港口代码前3位 + 时间戳后4位
        String originCode = originPort.getCode();
        String destCode = destinationPort.getCode();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmm"));
        
        // 确保港口代码长度
        String originPrefix = originCode.length() >= 3 ? originCode.substring(0, 3) : originCode;
        String destPrefix = destCode.length() >= 3 ? destCode.substring(0, 3) : destCode;
        
        return String.format("R%s%s%s", originPrefix, destPrefix, timestamp);
    }

    /**
     * 计算两港口间的距离（使用Haversine公式）
     */
    private BigDecimal calculateDistance(Port originPort, Port destinationPort) {
        if (originPort.getLatitude() == null || originPort.getLongitude() == null ||
            destinationPort.getLatitude() == null || destinationPort.getLongitude() == null) {
            // 如果港口坐标不完整，返回默认距离
            return new BigDecimal("1000.00");
        }

        double lat1 = originPort.getLatitude().doubleValue();
        double lon1 = originPort.getLongitude().doubleValue();
        double lat2 = destinationPort.getLatitude().doubleValue();
        double lon2 = destinationPort.getLongitude().doubleValue();

        // 地球半径（公里）
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        
        return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 根据距离计算预计航行时间
     */
    private BigDecimal calculateEstimatedDuration(BigDecimal distance) {
        // 假设平均航行速度为20公里/小时
        double avgSpeed = 20.0;
        double duration = distance.doubleValue() / avgSpeed;
        return new BigDecimal(duration).setScale(2, RoundingMode.HALF_UP);
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
            updateRoute.setDistanceNm(distanceNm.setScale(2, RoundingMode.HALF_UP));
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