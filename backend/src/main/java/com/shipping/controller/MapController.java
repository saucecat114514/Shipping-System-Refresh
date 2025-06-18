package com.shipping.controller;

import com.shipping.service.MapService;
import com.shipping.model.dto.LocationRequest;
import com.shipping.model.dto.RouteCalculationRequest;
import com.shipping.model.entity.MapRoute;
import com.shipping.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 地图API控制器
 */
@Tag(name = "地图API", description = "地图相关的API接口")
@RestController
@RequestMapping("/api/map")
public class MapController {

    @Autowired
    private MapService mapService;

    @Operation(summary = "地理编码", description = "将地址转换为坐标")
    @GetMapping("/geocode")
    public Result<LocationRequest> geocode(@Parameter(description = "地址") @RequestParam String address) {
        LocationRequest location = mapService.geocode(address);
        return Result.success(location);
    }

    @Operation(summary = "逆地理编码", description = "将坐标转换为地址")
    @PostMapping("/reverse-geocode")
    public Result<String> reverseGeocode(@Valid @RequestBody LocationRequest location) {
        String address = mapService.reverseGeocode(location);
        return Result.success(address);
    }

    @Operation(summary = "计算路径", description = "计算两点之间的最优路径")
    @PostMapping("/calculate-route")
    public Result<MapRoute> calculateRoute(@Valid @RequestBody RouteCalculationRequest request) {
        MapRoute route = mapService.calculateRoute(request);
        return Result.success(route);
    }

    @Operation(summary = "计算距离矩阵", description = "计算多个起点到多个终点的距离和时间")
    @PostMapping("/distance-matrix")
    public Result<Map<String, Object>> calculateDistanceMatrix(
            @Parameter(description = "起点列表") @RequestBody List<LocationRequest> origins,
            @Parameter(description = "终点列表") @RequestParam List<LocationRequest> destinations) {
        Map<String, Object> matrix = mapService.calculateDistanceMatrix(origins, destinations);
        return Result.success(matrix);
    }

    @Operation(summary = "获取交通信息", description = "获取指定区域的实时交通信息")
    @PostMapping("/traffic-info")
    public Result<Map<String, Object>> getTrafficInfo(
            @Valid @RequestBody LocationRequest center,
            @Parameter(description = "搜索半径(公里)") @RequestParam(defaultValue = "10") Integer radius) {
        Map<String, Object> trafficInfo = mapService.getTrafficInfo(center, radius);
        return Result.success(trafficInfo);
    }

    @Operation(summary = "搜索附近港口", description = "搜索指定位置附近的港口")
    @PostMapping("/nearby-ports")
    public Result<List<Map<String, Object>>> searchNearbyPorts(
            @Valid @RequestBody LocationRequest location,
            @Parameter(description = "搜索半径(公里)") @RequestParam(defaultValue = "50") Integer radius) {
        List<Map<String, Object>> ports = mapService.searchNearbyPorts(location, radius);
        return Result.success(ports);
    }

    @Operation(summary = "搜索附近服务设施", description = "搜索指定位置附近的服务设施")
    @PostMapping("/nearby-services")
    public Result<List<Map<String, Object>>> searchNearbyServices(
            @Valid @RequestBody LocationRequest location,
            @Parameter(description = "服务类型") @RequestParam String serviceType,
            @Parameter(description = "搜索半径(公里)") @RequestParam(defaultValue = "20") Integer radius) {
        List<Map<String, Object>> services = mapService.searchNearbyServices(location, serviceType, radius);
        return Result.success(services);
    }

    @Operation(summary = "获取海洋天气", description = "获取指定位置的海洋天气信息")
    @PostMapping("/marine-weather")
    public Result<Map<String, Object>> getMarineWeather(@Valid @RequestBody LocationRequest location) {
        Map<String, Object> weather = mapService.getMarineWeather(location);
        return Result.success(weather);
    }

    @Operation(summary = "获取潮汐信息", description = "获取指定位置的潮汐信息")
    @PostMapping("/tide-info")
    public Result<Map<String, Object>> getTideInfo(@Valid @RequestBody LocationRequest location) {
        Map<String, Object> tideInfo = mapService.getTideInfo(location);
        return Result.success(tideInfo);
    }

    @Operation(summary = "保存路径", description = "保存计算好的路径")
    @PostMapping("/routes")
    public Result<Void> saveRoute(@Valid @RequestBody MapRoute route) {
        mapService.saveRoute(route);
        return Result.success();
    }

    @Operation(summary = "获取保存的路径列表", description = "获取所有保存的路径")
    @GetMapping("/routes")
    public Result<List<MapRoute>> getSavedRoutes() {
        List<MapRoute> routes = mapService.getSavedRoutes();
        return Result.success(routes);
    }

    @Operation(summary = "获取路径详情", description = "根据ID获取路径详情")
    @GetMapping("/routes/{routeId}")
    public Result<MapRoute> getRouteById(@Parameter(description = "路径ID") @PathVariable Long routeId) {
        MapRoute route = mapService.getRouteById(routeId);
        return Result.success(route);
    }

    @Operation(summary = "删除路径", description = "删除保存的路径")
    @DeleteMapping("/routes/{routeId}")
    public Result<Void> deleteRoute(@Parameter(description = "路径ID") @PathVariable Long routeId) {
        mapService.deleteRoute(routeId);
        return Result.success();
    }

    @Operation(summary = "优化多点路径", description = "对多个地点进行路径优化")
    @PostMapping("/optimize-route")
    public Result<MapRoute> optimizeMultiPointRoute(
            @Parameter(description = "地点列表") @RequestBody List<LocationRequest> locations,
            @Parameter(description = "车辆类型") @RequestParam(defaultValue = "ship") String vehicleType) {
        MapRoute optimizedRoute = mapService.optimizeMultiPointRoute(locations, vehicleType);
        return Result.success(optimizedRoute);
    }

    @Operation(summary = "计算燃料消耗", description = "计算路径的燃料消耗")
    @PostMapping("/fuel-consumption")
    public Result<Map<String, Object>> calculateFuelConsumption(
            @RequestBody MapRoute route,
            @Parameter(description = "车辆类型") @RequestParam String vehicleType,
            @Parameter(description = "燃料效率(公里/升)") @RequestParam Double fuelEfficiency) {
        Map<String, Object> fuelInfo = mapService.calculateFuelConsumption(route, vehicleType, fuelEfficiency);
        return Result.success(fuelInfo);
    }

    @Operation(summary = "计算路径费用", description = "计算路径的运输费用")
    @PostMapping("/route-cost")
    public Result<Map<String, Object>> calculateRouteCost(
            @RequestBody MapRoute route,
            @Parameter(description = "车辆类型") @RequestParam String vehicleType) {
        Map<String, Object> costInfo = mapService.calculateRouteCost(route, vehicleType);
        return Result.success(costInfo);
    }

    @Operation(summary = "检查航道限制", description = "检查船舶在指定路径上的航道限制")
    @PostMapping("/navigation-restrictions")
    public Result<Map<String, Object>> checkNavigationRestrictions(
            @RequestBody MapRoute route,
            @Parameter(description = "船舶类型") @RequestParam String shipType,
            @Parameter(description = "船长(米)") @RequestParam Double shipLength,
            @Parameter(description = "船宽(米)") @RequestParam Double shipWidth) {
        Map<String, Object> restrictions = mapService.checkNavigationRestrictions(route, shipType, shipLength, shipWidth);
        return Result.success(restrictions);
    }
} 