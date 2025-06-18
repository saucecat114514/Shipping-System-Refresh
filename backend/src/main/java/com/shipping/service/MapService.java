package com.shipping.service;

import com.shipping.model.dto.LocationRequest;
import com.shipping.model.dto.RouteCalculationRequest;
import com.shipping.model.entity.MapRoute;
import java.util.List;
import java.util.Map;

/**
 * 地图服务接口
 */
public interface MapService {

    /**
     * 地理编码 - 地址转坐标
     */
    LocationRequest geocode(String address);

    /**
     * 逆地理编码 - 坐标转地址
     */
    String reverseGeocode(LocationRequest location);

    /**
     * 计算路径
     */
    MapRoute calculateRoute(RouteCalculationRequest request);

    /**
     * 计算多个地点之间的距离矩阵
     */
    Map<String, Object> calculateDistanceMatrix(List<LocationRequest> origins, List<LocationRequest> destinations);

    /**
     * 获取实时交通信息
     */
    Map<String, Object> getTrafficInfo(LocationRequest center, Integer radius);

    /**
     * 搜索附近的港口
     */
    List<Map<String, Object>> searchNearbyPorts(LocationRequest location, Integer radius);

    /**
     * 搜索附近的服务设施
     */
    List<Map<String, Object>> searchNearbyServices(LocationRequest location, String serviceType, Integer radius);

    /**
     * 获取海洋天气信息
     */
    Map<String, Object> getMarineWeather(LocationRequest location);

    /**
     * 获取潮汐信息
     */
    Map<String, Object> getTideInfo(LocationRequest location);

    /**
     * 保存计算好的路径
     */
    void saveRoute(MapRoute route);

    /**
     * 获取保存的路径列表
     */
    List<MapRoute> getSavedRoutes();

    /**
     * 根据ID获取路径详情
     */
    MapRoute getRouteById(Long routeId);

    /**
     * 删除保存的路径
     */
    void deleteRoute(Long routeId);

    /**
     * 优化多点路径
     */
    MapRoute optimizeMultiPointRoute(List<LocationRequest> locations, String vehicleType);

    /**
     * 获取路径的燃料消耗估算
     */
    Map<String, Object> calculateFuelConsumption(MapRoute route, String vehicleType, Double fuelEfficiency);

    /**
     * 获取路径的费用估算
     */
    Map<String, Object> calculateRouteCost(MapRoute route, String vehicleType);

    /**
     * 检查航道限制
     */
    Map<String, Object> checkNavigationRestrictions(MapRoute route, String shipType, Double shipLength, Double shipWidth);
} 