package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Route;
import com.shipping.model.dto.RouteRequest;
import com.shipping.model.dto.RouteQueryRequest;
import com.shipping.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 航线管理控制器
 */
@Tag(name = "航线管理", description = "航线信息的增删改查操作")
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * 创建航线
     */
    @Operation(summary = "创建航线", description = "添加新的航线信息")
    @PostMapping
    public Result<Route> createRoute(@Valid @RequestBody RouteRequest routeRequest) {
        return routeService.createRoute(routeRequest);
    }

    /**
     * 删除航线
     */
    @Operation(summary = "删除航线", description = "根据ID删除航线")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoute(
            @Parameter(description = "航线ID") @PathVariable Long id) {
        return routeService.deleteRoute(id);
    }

    /**
     * 更新航线信息
     */
    @Operation(summary = "更新航线", description = "更新航线信息")
    @PutMapping("/{id}")
    public Result<Route> updateRoute(
            @Parameter(description = "航线ID") @PathVariable Long id,
            @Valid @RequestBody RouteRequest routeRequest) {
        return routeService.updateRoute(id, routeRequest);
    }

    /**
     * 根据ID查询航线
     */
    @Operation(summary = "查询航线详情", description = "根据ID查询航线详细信息")
    @GetMapping("/{id}")
    public Result<Route> getRouteById(
            @Parameter(description = "航线ID") @PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    /**
     * 根据航线编号查询航线
     */
    @Operation(summary = "根据编号查询航线", description = "根据航线编号查询航线信息")
    @GetMapping("/number/{routeNumber}")
    public Result<Route> getRouteByRouteNumber(
            @Parameter(description = "航线编号") @PathVariable String routeNumber) {
        return routeService.getRouteByRouteNumber(routeNumber);
    }

    /**
     * 分页查询航线列表
     */
    @Operation(summary = "分页查询航线", description = "分页查询航线列表，支持按编号、名称、港口等筛选")
    @GetMapping
    public Result<PageResult<Route>> getRoutePage(RouteQueryRequest queryRequest) {
        return routeService.getRoutePage(queryRequest);
    }

    /**
     * 分页查询航线列表（包含港口信息）
     */
    @Operation(summary = "分页查询航线（含港口信息）", description = "分页查询航线列表，包含关联的港口详细信息")
    @GetMapping("/with-ports")
    public Result<PageResult<Route>> getRoutePageWithPorts(RouteQueryRequest queryRequest) {
        return routeService.getRoutePageWithPorts(queryRequest);
    }

    /**
     * 获取所有航线列表
     */
    @Operation(summary = "获取所有航线", description = "获取所有启用的航线列表，用于下拉框等")
    @GetMapping("/all")
    public Result<List<Route>> getAllRoutes() {
        return routeService.getAllRoutes();
    }
} 