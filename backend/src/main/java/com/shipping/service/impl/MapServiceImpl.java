package com.shipping.service.impl;

import com.shipping.service.MapService;
import com.shipping.model.dto.LocationRequest;
import com.shipping.model.dto.RouteCalculationRequest;
import com.shipping.model.entity.MapRoute;
import com.shipping.mapper.MapRouteMapper;
import com.shipping.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 地图服务实现类
 */
@Service
public class MapServiceImpl implements MapService {

    private static final Logger logger = LoggerFactory.getLogger(MapServiceImpl.class);

    @Autowired
    private MapRouteMapper mapRouteMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.web-api-url}")
    private String amapWebApiUrl;

    // 地球半径（公里）
    private static final double EARTH_RADIUS = 6371.0;

    @Override
    public LocationRequest geocode(String address) {
        logger.info("地理编码查询：{}", address);
        
        try {
            // 调用高德地图地理编码API
            String url = String.format("%s/geocode/geo?key=%s&address=%s", 
                amapWebApiUrl, amapKey, address);
            
            logger.info("调用高德地图API: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "1".equals(response.get("status"))) {
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) response.get("geocodes");
                if (geocodes != null && !geocodes.isEmpty()) {
                    Map<String, Object> geocode = geocodes.get(0);
                    String location = (String) geocode.get("location");
                    if (location != null && !location.isEmpty()) {
                        String[] coords = location.split(",");
                        BigDecimal longitude = new BigDecimal(coords[0]);
                        BigDecimal latitude = new BigDecimal(coords[1]);
                        return new LocationRequest(longitude, latitude);
                    }
                }
            }
            
            logger.warn("地理编码失败，返回默认坐标（上海）");
            // 如果API调用失败，返回默认坐标（上海）
            return new LocationRequest(new BigDecimal("121.4648"), new BigDecimal("31.2304"));
            
        } catch (Exception e) {
            logger.error("地理编码调用失败：{}", e.getMessage());
            // 调用失败时返回默认坐标
            return new LocationRequest(new BigDecimal("121.4648"), new BigDecimal("31.2304"));
        }
    }

    @Override
    public String reverseGeocode(LocationRequest location) {
        logger.info("逆地理编码查询：经度{}，纬度{}", location.getLongitude(), location.getLatitude());
        
        try {
            // 调用高德地图逆地理编码API
            String url = String.format("%s/geocode/regeo?key=%s&location=%s,%s", 
                amapWebApiUrl, amapKey, 
                location.getLongitude(), location.getLatitude());
            
            logger.info("调用高德地图API: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "1".equals(response.get("status"))) {
                Map<String, Object> regeocode = (Map<String, Object>) response.get("regeocode");
                if (regeocode != null) {
                    String formattedAddress = (String) regeocode.get("formatted_address");
                    if (formattedAddress != null && !formattedAddress.isEmpty()) {
                        return formattedAddress;
                    }
                }
            }
            
            return "未知位置";
            
        } catch (Exception e) {
            logger.error("逆地理编码调用失败：{}", e.getMessage());
            return "位置解析失败";
        }
    }

    @Override
    public MapRoute calculateRoute(RouteCalculationRequest request) {
        logger.info("计算路径：从 [{}, {}] 到 [{}, {}]", 
            request.getStartPoint().getLongitude(), request.getStartPoint().getLatitude(),
            request.getEndPoint().getLongitude(), request.getEndPoint().getLatitude());
        
        try {
            MapRoute route = new MapRoute();
            
            // 设置起点和终点
            route.setStartLongitude(request.getStartPoint().getLongitude());
            route.setStartLatitude(request.getStartPoint().getLatitude());
            route.setStartAddress(request.getStartPoint().getAddress());
            route.setEndLongitude(request.getEndPoint().getLongitude());
            route.setEndLatitude(request.getEndPoint().getLatitude());
            route.setEndAddress(request.getEndPoint().getAddress());
            
            // 计算距离
            BigDecimal distance = calculateDistance(
                request.getStartPoint().getLatitude().doubleValue(),
                request.getStartPoint().getLongitude().doubleValue(),
                request.getEndPoint().getLatitude().doubleValue(),
                request.getEndPoint().getLongitude().doubleValue()
            );
            route.setDistance(distance);
            
            // 根据路径类型和车辆类型计算预计时间
            BigDecimal estimatedDuration = calculateEstimatedDuration(distance, request.getRouteType(), request.getVehicleType());
            route.setEstimatedDuration(estimatedDuration);
            
            // 设置路径属性
            route.setRouteType(request.getRouteType());
            route.setVehicleType(request.getVehicleType());
            route.setShipType(request.getShipType());
            
            // 模拟路径坐标
            String routeCoordinates = generateRouteCoordinates(request.getStartPoint(), request.getEndPoint());
            route.setRouteCoordinates(routeCoordinates);
            
            // 计算燃料消耗
            BigDecimal fuelConsumption = calculateBasicFuelConsumption(distance, request.getVehicleType());
            route.setFuelConsumption(fuelConsumption);
            
            // 计算路径费用
            BigDecimal routeCost = calculateBasicRouteCost(distance, request.getVehicleType());
            route.setRouteCost(routeCost);
            
            route.setStatus("CALCULATED");
            route.setCreatedAt(LocalDateTime.now());
            route.setUpdatedAt(LocalDateTime.now());
            
            logger.info("路径计算完成：距离{}公里，预计时间{}小时", distance, estimatedDuration);
            return route;
            
        } catch (Exception e) {
            logger.error("路径计算失败：{}", e.getMessage());
            throw new BusinessException("路径计算服务异常");
        }
    }

    @Override
    public Map<String, Object> calculateDistanceMatrix(List<LocationRequest> origins, List<LocationRequest> destinations) {
        logger.info("计算距离矩阵：起点数量{}，终点数量{}", origins.size(), destinations.size());
        
        Map<String, Object> result = new HashMap<>();
        List<List<Map<String, Object>>> matrix = new ArrayList<>();
        
        for (LocationRequest origin : origins) {
            List<Map<String, Object>> row = new ArrayList<>();
            for (LocationRequest destination : destinations) {
                Map<String, Object> element = new HashMap<>();
                
                BigDecimal distance = calculateDistance(
                    origin.getLatitude().doubleValue(),
                    origin.getLongitude().doubleValue(),
                    destination.getLatitude().doubleValue(),
                    destination.getLongitude().doubleValue()
                );
                
                BigDecimal duration = calculateEstimatedDuration(distance, "fastest", "ship");
                
                element.put("distance", distance);
                element.put("duration", duration);
                element.put("status", "OK");
                row.add(element);
            }
            matrix.add(row);
        }
        
        result.put("origins", origins);
        result.put("destinations", destinations);
        result.put("rows", matrix);
        result.put("status", "OK");
        
        return result;
    }

    @Override
    public Map<String, Object> getTrafficInfo(LocationRequest center, Integer radius) {
        logger.info("获取交通信息：中心点[{}, {}]，半径{}公里", 
            center.getLongitude(), center.getLatitude(), radius);
        
        Map<String, Object> trafficInfo = new HashMap<>();
        trafficInfo.put("center", center);
        trafficInfo.put("radius", radius);
        trafficInfo.put("trafficLevel", "smooth"); // smooth, slow, congested
        trafficInfo.put("averageSpeed", 25.5); // km/h
        trafficInfo.put("incidents", new ArrayList<>()); // 交通事故信息
        trafficInfo.put("roadworks", new ArrayList<>()); // 道路施工信息
        trafficInfo.put("weatherCondition", "clear"); // 天气状况
        trafficInfo.put("visibility", 10.0); // 能见度(公里)
        trafficInfo.put("timestamp", LocalDateTime.now());
        
        return trafficInfo;
    }

    @Override
    public List<Map<String, Object>> searchNearbyPorts(LocationRequest location, Integer radius) {
        logger.info("搜索附近港口：位置[{}, {}]，半径{}公里", 
            location.getLongitude(), location.getLatitude(), radius);
        
        List<Map<String, Object>> ports = new ArrayList<>();
        
        // 模拟港口数据
        Map<String, Object> port1 = new HashMap<>();
        port1.put("id", 1L);
        port1.put("name", "上海洋山深水港");
        port1.put("longitude", new BigDecimal("121.9317"));
        port1.put("latitude", new BigDecimal("30.6316"));
        port1.put("type", "deep_water");
        port1.put("services", Arrays.asList("container", "bulk", "liquid"));
        port1.put("distance", 15.2);
        ports.add(port1);
        
        Map<String, Object> port2 = new HashMap<>();
        port2.put("id", 2L);
        port2.put("name", "上海外高桥港区");
        port2.put("longitude", new BigDecimal("121.5742"));
        port2.put("latitude", new BigDecimal("31.3639"));
        port2.put("type", "container");
        port2.put("services", Arrays.asList("container", "general"));
        port2.put("distance", 8.7);
        ports.add(port2);
        
        return ports;
    }

    @Override
    public List<Map<String, Object>> searchNearbyServices(LocationRequest location, String serviceType, Integer radius) {
        logger.info("搜索附近服务设施：位置[{}, {}]，服务类型{}，半径{}公里", 
            location.getLongitude(), location.getLatitude(), serviceType, radius);
        
        List<Map<String, Object>> services = new ArrayList<>();
        
        // 根据服务类型返回模拟数据
        if ("fuel".equals(serviceType)) {
            Map<String, Object> service = new HashMap<>();
            service.put("id", 1L);
            service.put("name", "中石化加油站");
            service.put("type", "fuel");
            service.put("longitude", new BigDecimal("121.4800"));
            service.put("latitude", new BigDecimal("31.2400"));
            service.put("distance", 2.5);
            service.put("available", true);
            services.add(service);
        }
        
        return services;
    }

    @Override
    public Map<String, Object> getMarineWeather(LocationRequest location) {
        logger.info("获取海洋天气：位置[{}, {}]", location.getLongitude(), location.getLatitude());
        
        Map<String, Object> weather = new HashMap<>();
        weather.put("location", location);
        weather.put("temperature", 22.5); // 摄氏度
        weather.put("humidity", 75); // 湿度百分比
        weather.put("windSpeed", 12.3); // 风速 km/h
        weather.put("windDirection", "northeast"); // 风向
        weather.put("waveHeight", 1.2); // 波高 米
        weather.put("visibility", 8.5); // 能见度 公里
        weather.put("pressure", 1013.2); // 气压 hPa
        weather.put("condition", "partly_cloudy"); // 天气状况
        weather.put("timestamp", LocalDateTime.now());
        
        return weather;
    }

    @Override
    public Map<String, Object> getTideInfo(LocationRequest location) {
        logger.info("获取潮汐信息：位置[{}, {}]", location.getLongitude(), location.getLatitude());
        
        Map<String, Object> tideInfo = new HashMap<>();
        tideInfo.put("location", location);
        tideInfo.put("currentTideLevel", 2.3); // 当前潮位 米
        tideInfo.put("nextHighTide", LocalDateTime.now().plusHours(3)); // 下次高潮时间
        tideInfo.put("nextLowTide", LocalDateTime.now().plusHours(9)); // 下次低潮时间
        tideInfo.put("tidalRange", 3.8); // 潮差 米
        tideInfo.put("timestamp", LocalDateTime.now());
        
        return tideInfo;
    }

    @Override
    @Transactional
    public void saveRoute(MapRoute route) {
        logger.info("保存路径：{}", route.getRouteName());
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        mapRouteMapper.insert(route);
        logger.info("路径保存成功，ID：{}", route.getId());
    }

    @Override
    public List<MapRoute> getSavedRoutes() {
        logger.info("获取保存的路径列表");
        return mapRouteMapper.selectAll();
    }

    @Override
    public MapRoute getRouteById(Long routeId) {
        logger.info("获取路径详情，ID：{}", routeId);
        MapRoute route = mapRouteMapper.selectById(routeId);
        if (route == null) {
            throw new BusinessException("路径不存在");
        }
        return route;
    }

    @Override
    @Transactional
    public void deleteRoute(Long routeId) {
        logger.info("删除路径，ID：{}", routeId);
        mapRouteMapper.deleteById(routeId);
        logger.info("路径删除成功");
    }

    @Override
    public MapRoute optimizeMultiPointRoute(List<LocationRequest> locations, String vehicleType) {
        logger.info("优化多点路径：地点数量{}，车辆类型{}", locations.size(), vehicleType);
        
        // 简化的TSP（旅行商问题）求解
        // 实际实现中应该使用更复杂的优化算法
        
        if (locations.size() < 2) {
            throw new BusinessException("至少需要2个地点");
        }
        
        // 这里简单地按照最近邻算法优化路径
        List<LocationRequest> optimizedOrder = nearestNeighborOptimization(locations);
        
        // 创建优化后的路径
        MapRoute route = new MapRoute();
        route.setStartLongitude(optimizedOrder.get(0).getLongitude());
        route.setStartLatitude(optimizedOrder.get(0).getLatitude());
        route.setEndLongitude(optimizedOrder.get(optimizedOrder.size() - 1).getLongitude());
        route.setEndLatitude(optimizedOrder.get(optimizedOrder.size() - 1).getLatitude());
        route.setVehicleType(vehicleType);
        route.setRouteType("optimized");
        
        // 计算总距离
        BigDecimal totalDistance = BigDecimal.ZERO;
        for (int i = 0; i < optimizedOrder.size() - 1; i++) {
            LocationRequest current = optimizedOrder.get(i);
            LocationRequest next = optimizedOrder.get(i + 1);
            BigDecimal segmentDistance = calculateDistance(
                current.getLatitude().doubleValue(),
                current.getLongitude().doubleValue(),
                next.getLatitude().doubleValue(),
                next.getLongitude().doubleValue()
            );
            totalDistance = totalDistance.add(segmentDistance);
        }
        
        route.setDistance(totalDistance);
        route.setEstimatedDuration(calculateEstimatedDuration(totalDistance, "optimized", vehicleType));
        route.setStatus("OPTIMIZED");
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        
        logger.info("多点路径优化完成：总距离{}公里", totalDistance);
        return route;
    }

    @Override
    public Map<String, Object> calculateFuelConsumption(MapRoute route, String vehicleType, Double fuelEfficiency) {
        logger.info("计算燃料消耗：距离{}公里，车辆类型{}，燃料效率{}", 
            route.getDistance(), vehicleType, fuelEfficiency);
        
        Map<String, Object> result = new HashMap<>();
        
        double distance = route.getDistance().doubleValue();
        double consumption = distance / fuelEfficiency; // 基本消耗计算
        
        // 根据车辆类型调整
        switch (vehicleType.toLowerCase()) {
            case "ship":
                consumption *= 1.5; // 船舶消耗较高
                break;
            case "truck":
                consumption *= 1.2;
                break;
            case "car":
                // 保持基本消耗
                break;
        }
        
        result.put("totalFuelConsumption", consumption);
        result.put("fuelEfficiency", fuelEfficiency);
        result.put("distance", distance);
        result.put("vehicleType", vehicleType);
        result.put("estimatedCost", consumption * 6.5); // 假设燃料价格每升6.5元
        
        return result;
    }

    @Override
    public Map<String, Object> calculateRouteCost(MapRoute route, String vehicleType) {
        logger.info("计算路径费用：距离{}公里，车辆类型{}", route.getDistance(), vehicleType);
        
        Map<String, Object> result = new HashMap<>();
        
        double distance = route.getDistance().doubleValue();
        double baseCost = 0;
        
        // 根据车辆类型设置基本费率
        switch (vehicleType.toLowerCase()) {
            case "ship":
                baseCost = distance * 5.0; // 每公里5元
                break;
            case "truck":
                baseCost = distance * 3.0; // 每公里3元
                break;
            case "car":
                baseCost = distance * 2.0; // 每公里2元
                break;
        }
        
        // 添加其他费用
        double fuelCost = distance * 1.5; // 燃料费
        double tolls = distance * 0.5; // 过路费
        double serviceFee = baseCost * 0.1; // 服务费
        
        double totalCost = baseCost + fuelCost + tolls + serviceFee;
        
        result.put("baseCost", baseCost);
        result.put("fuelCost", fuelCost);
        result.put("tolls", tolls);
        result.put("serviceFee", serviceFee);
        result.put("totalCost", totalCost);
        result.put("currency", "CNY");
        
        return result;
    }

    @Override
    public Map<String, Object> checkNavigationRestrictions(MapRoute route, String shipType, Double shipLength, Double shipWidth) {
        logger.info("检查航道限制：船舶类型{}，长度{}米，宽度{}米", shipType, shipLength, shipWidth);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> restrictions = new ArrayList<>();
        
        // 模拟航道限制检查
        if (shipLength > 400) {
            Map<String, Object> restriction = new HashMap<>();
            restriction.put("type", "length_limit");
            restriction.put("description", "部分港口对船长超过400米的船舶有限制");
            restriction.put("severity", "warning");
            restrictions.add(restriction);
        }
        
        if (shipWidth > 60) {
            Map<String, Object> restriction = new HashMap<>();
            restriction.put("type", "width_limit");
            restriction.put("description", "某些航道对船宽超过60米的船舶有限制");
            restriction.put("severity", "warning");
            restrictions.add(restriction);
        }
        
        // 检查特殊货物限制
        if ("tanker".equals(shipType)) {
            Map<String, Object> restriction = new HashMap<>();
            restriction.put("type", "cargo_restriction");
            restriction.put("description", "油轮需要特殊许可和指定航道");
            restriction.put("severity", "info");
            restrictions.add(restriction);
        }
        
        result.put("hasRestrictions", !restrictions.isEmpty());
        result.put("restrictions", restrictions);
        result.put("overallStatus", restrictions.isEmpty() ? "clear" : "restricted");
        
        return result;
    }

    /**
     * 计算两点间距离（使用Haversine公式）
     */
    private BigDecimal calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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
     * 计算预计时间
     */
    private BigDecimal calculateEstimatedDuration(BigDecimal distance, String routeType, String vehicleType) {
        double distanceKm = distance.doubleValue();
        double speed = 25.0; // 默认速度 km/h
        
        // 根据车辆类型调整速度
        switch (vehicleType.toLowerCase()) {
            case "ship":
                speed = 20.0; // 船舶速度较慢
                break;
            case "truck":
                speed = 60.0;
                break;
            case "car":
                speed = 80.0;
                break;
        }
        
        // 根据路径类型调整
        switch (routeType.toLowerCase()) {
            case "fastest":
                speed *= 1.1;
                break;
            case "economic":
                speed *= 0.9;
                break;
        }
        
        double duration = distanceKm / speed;
        return new BigDecimal(duration).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成路径坐标（模拟）
     */
    private String generateRouteCoordinates(LocationRequest start, LocationRequest end) {
        return String.format("[{\"lng\":%s,\"lat\":%s},{\"lng\":%s,\"lat\":%s}]",
            start.getLongitude(), start.getLatitude(),
            end.getLongitude(), end.getLatitude());
    }

    /**
     * 计算基本燃料消耗
     */
    private BigDecimal calculateBasicFuelConsumption(BigDecimal distance, String vehicleType) {
        double distanceKm = distance.doubleValue();
        double consumption = 0;
        
        switch (vehicleType.toLowerCase()) {
            case "ship":
                consumption = distanceKm * 2.5; // 每公里2.5升
                break;
            case "truck":
                consumption = distanceKm * 0.35; // 每公里0.35升
                break;
            case "car":
                consumption = distanceKm * 0.08; // 每公里0.08升
                break;
        }
        
        return new BigDecimal(consumption).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算基本路径费用
     */
    private BigDecimal calculateBasicRouteCost(BigDecimal distance, String vehicleType) {
        double distanceKm = distance.doubleValue();
        double cost = 0;
        
        switch (vehicleType.toLowerCase()) {
            case "ship":
                cost = distanceKm * 8.0; // 每公里8元
                break;
            case "truck":
                cost = distanceKm * 5.0; // 每公里5元
                break;
            case "car":
                cost = distanceKm * 3.0; // 每公里3元
                break;
        }
        
        return new BigDecimal(cost).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 最近邻优化算法
     */
    private List<LocationRequest> nearestNeighborOptimization(List<LocationRequest> locations) {
        if (locations.size() <= 2) {
            return new ArrayList<>(locations);
        }
        
        List<LocationRequest> result = new ArrayList<>();
        List<LocationRequest> remaining = new ArrayList<>(locations);
        
        // 从第一个点开始
        LocationRequest current = remaining.remove(0);
        result.add(current);
        
        while (!remaining.isEmpty()) {
            LocationRequest nearest = null;
            double minDistance = Double.MAX_VALUE;
            
            for (LocationRequest location : remaining) {
                double distance = calculateDistance(
                    current.getLatitude().doubleValue(),
                    current.getLongitude().doubleValue(),
                    location.getLatitude().doubleValue(),
                    location.getLongitude().doubleValue()
                ).doubleValue();
                
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = location;
                }
            }
            
            if (nearest != null) {
                result.add(nearest);
                remaining.remove(nearest);
                current = nearest;
            }
        }
        
        return result;
    }
} 