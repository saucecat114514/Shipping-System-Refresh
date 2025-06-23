package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import com.shipping.model.entity.Voyage;
import com.shipping.model.dto.VoyageRequest;
import com.shipping.model.dto.VoyageQueryRequest;
import com.shipping.service.VoyageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 航次管理控制器
 * 管理员和调度员可以管理航次，客户只能查看
 */
@Tag(name = "航次管理", description = "航次信息的增删改查操作")
@RestController
@RequestMapping("/voyages")
public class VoyageController {

    @Autowired
    private VoyageService voyageService;

    /**
     * 创建航次
     */
    @Operation(summary = "创建航次", description = "添加新的航次信息")
    @PostMapping
    public Result<Voyage> createVoyage(@Valid @RequestBody VoyageRequest voyageRequest) {
        return voyageService.createVoyage(voyageRequest);
    }

    /**
     * 删除航次
     */
    @Operation(summary = "删除航次", description = "根据ID删除航次")
    @DeleteMapping("/{id}")
    public Result<Void> deleteVoyage(
            @Parameter(description = "航次ID") @PathVariable Long id) {
        return voyageService.deleteVoyage(id);
    }

    /**
     * 更新航次信息
     */
    @Operation(summary = "更新航次", description = "更新航次信息")
    @PutMapping("/{id}")
    public Result<Voyage> updateVoyage(
            @Parameter(description = "航次ID") @PathVariable Long id,
            @Valid @RequestBody VoyageRequest voyageRequest) {
        return voyageService.updateVoyage(id, voyageRequest);
    }

    /**
     * 根据ID查询航次
     */
    @Operation(summary = "查询航次详情", description = "根据ID查询航次详细信息")
    @GetMapping("/{id}")
    public Result<Voyage> getVoyageById(
            @Parameter(description = "航次ID") @PathVariable Long id) {
        return voyageService.getVoyageById(id);
    }

    /**
     * 根据编号查询航次
     */
    @Operation(summary = "根据编号查询航次", description = "根据航次编号查询航次信息")
    @GetMapping("/number/{voyageNumber}")
    public Result<Voyage> getVoyageByVoyageNumber(
            @Parameter(description = "航次编号") @PathVariable String voyageNumber) {
        return voyageService.getVoyageByVoyageNumber(voyageNumber);
    }

    /**
     * 分页查询航次列表
     */
    @Operation(summary = "分页查询航次", description = "分页查询航次列表，支持按编号、航线、船舶等筛选")
    @GetMapping
    public Result<PageResult<Voyage>> getVoyagePage(VoyageQueryRequest queryRequest) {
        return voyageService.getVoyagePageWithDetails(queryRequest);
    }

    /**
     * 分页查询航次列表（包含关联信息）
     */
    @Operation(summary = "分页查询航次（含关联信息）", description = "分页查询航次列表，包含关联的航线和船舶详细信息")
    @GetMapping("/with-details")
    public Result<PageResult<Voyage>> getVoyagePageWithDetails(VoyageQueryRequest queryRequest) {
        return voyageService.getVoyagePageWithDetails(queryRequest);
    }

    /**
     * 获取所有启用的航次
     */
    @Operation(summary = "获取所有启用的航次", description = "获取所有启用的航次列表，用于下拉框等")
    @GetMapping("/active")
    public Result<List<Voyage>> getAllActiveVoyages() {
        return voyageService.getAllActiveVoyages();
    }

    /**
     * 根据航线ID查询航次列表
     */
    @Operation(summary = "根据航线查询航次", description = "根据航线ID查询该航线的所有航次")
    @GetMapping("/route/{routeId}")
    public Result<List<Voyage>> getVoyagesByRouteId(
            @Parameter(description = "航线ID") @PathVariable Long routeId) {
        return voyageService.getVoyagesByRouteId(routeId);
    }

    /**
     * 根据船舶ID查询航次列表
     */
    @Operation(summary = "根据船舶查询航次", description = "根据船舶ID查询该船舶的所有航次")
    @GetMapping("/ship/{shipId}")
    public Result<List<Voyage>> getVoyagesByShipId(
            @Parameter(description = "船舶ID") @PathVariable Long shipId) {
        return voyageService.getVoyagesByShipId(shipId);
    }

    /**
     * 更新航次状态
     */
    @Operation(summary = "更新航次状态", description = "更新航次的状态（PLANNED/IN_PROGRESS/COMPLETED/CANCELLED）")
    @PatchMapping("/{id}/status")
    public Result<Void> updateVoyageStatus(
            @Parameter(description = "航次ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        return voyageService.updateVoyageStatus(id, status);
    }

    /**
     * 自动生成航次
     */
    @Operation(summary = "自动生成航次", description = "基于航线和船舶自动生成航次")
    @PostMapping("/generate")
    public Result<Voyage> generateVoyage(
            @Parameter(description = "航线ID") @RequestParam Long routeId,
            @Parameter(description = "船舶ID") @RequestParam Long shipId) {
        return voyageService.generateVoyage(routeId, shipId);
    }
} 