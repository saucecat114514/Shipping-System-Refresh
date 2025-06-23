package com.shipping.controller;

import com.shipping.service.SystemConfigService;
import com.shipping.model.entity.SystemConfig;
import com.shipping.model.dto.SystemConfigRequest;
import com.shipping.model.dto.SystemConfigQueryRequest;
import com.shipping.common.Result;
import com.shipping.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 */
@Tag(name = "系统配置管理", description = "系统配置管理相关接口")
@RestController
@RequestMapping("/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Operation(summary = "添加系统配置", description = "添加新的系统配置")
    @PostMapping
    public Result<Void> addSystemConfig(@Valid @RequestBody SystemConfigRequest request) {
        systemConfigService.addSystemConfig(request);
        return Result.success();
    }

    @Operation(summary = "更新系统配置", description = "更新指定ID的系统配置")
    @PutMapping("/{id}")
    public Result<Void> updateSystemConfig(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Valid @RequestBody SystemConfigRequest request) {
        systemConfigService.updateSystemConfig(id, request);
        return Result.success();
    }

    @Operation(summary = "根据ID查询系统配置", description = "根据ID查询系统配置详情")
    @GetMapping("/{id}")
    public Result<SystemConfig> getSystemConfigById(@Parameter(description = "配置ID") @PathVariable Long id) {
        SystemConfig systemConfig = systemConfigService.getSystemConfigById(id);
        return Result.success(systemConfig);
    }

    @Operation(summary = "根据配置键查询", description = "根据配置键查询系统配置")
    @GetMapping("/key/{configKey}")
    public Result<SystemConfig> getSystemConfigByKey(@Parameter(description = "配置键") @PathVariable String configKey) {
        SystemConfig systemConfig = systemConfigService.getSystemConfigByKey(configKey);
        return Result.success(systemConfig);
    }

    @Operation(summary = "获取配置值", description = "根据配置键获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(@Parameter(description = "配置键") @PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    @Operation(summary = "分页查询系统配置", description = "分页查询系统配置列表")
    @GetMapping
    public Result<PageResult<SystemConfig>> getSystemConfigPage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "配置键（模糊查询）") @RequestParam(required = false) String configKey,
            @Parameter(description = "配置值（模糊查询）") @RequestParam(required = false) String configValue,
            @Parameter(description = "描述（模糊查询）") @RequestParam(required = false) String description,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "config_key") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        SystemConfigQueryRequest query = new SystemConfigQueryRequest();
        query.setPage(page);
        query.setSize(size);
        query.setConfigKey(configKey);
        query.setConfigValue(configValue);
        query.setDescription(description);
        query.setSortField(sortField);
        query.setSortDirection(sortDirection);
        
        PageResult<SystemConfig> result = systemConfigService.getSystemConfigPage(query);
        return Result.success(result);
    }

    @Operation(summary = "查询所有系统配置", description = "查询所有系统配置列表")
    @GetMapping("/all")
    public Result<List<SystemConfig>> getAllSystemConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllSystemConfigs();
        return Result.success(configs);
    }

    @Operation(summary = "获取配置映射", description = "获取系统配置的键值对映射")
    @GetMapping("/map")
    public Result<Map<String, String>> getSystemConfigMap() {
        Map<String, String> configMap = systemConfigService.getSystemConfigMap();
        return Result.success(configMap);
    }

    @Operation(summary = "删除系统配置", description = "根据ID删除系统配置")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSystemConfig(@Parameter(description = "配置ID") @PathVariable Long id) {
        systemConfigService.deleteSystemConfig(id);
        return Result.success();
    }

    @Operation(summary = "根据配置键删除", description = "根据配置键删除系统配置")
    @DeleteMapping("/key/{configKey}")
    public Result<Void> deleteSystemConfigByKey(@Parameter(description = "配置键") @PathVariable String configKey) {
        systemConfigService.deleteSystemConfigByKey(configKey);
        return Result.success();
    }

    @Operation(summary = "批量删除系统配置", description = "批量删除系统配置")
    @DeleteMapping("/batch")
    public Result<Void> deleteSystemConfigBatch(@RequestBody List<Long> ids) {
        systemConfigService.deleteSystemConfigBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新配置值", description = "根据配置键更新配置值")
    @PutMapping("/value/{configKey}")
    public Result<Void> updateConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "新的配置值") @RequestBody String configValue) {
        systemConfigService.updateConfigValue(configKey, configValue);
        return Result.success();
    }

    @Operation(summary = "批量更新系统配置", description = "批量更新系统配置")
    @PutMapping("/batch")
    public Result<Void> updateSystemConfigBatch(@Valid @RequestBody List<SystemConfigRequest> requests) {
        systemConfigService.updateSystemConfigBatch(requests);
        return Result.success();
    }

    @Operation(summary = "初始化默认配置", description = "初始化系统默认配置")
    @PostMapping("/init-defaults")
    public Result<Void> initDefaultConfigs() {
        systemConfigService.initDefaultConfigs();
        return Result.success();
    }

    @Operation(summary = "重置系统配置", description = "重置为默认系统配置（危险操作）")
    @PostMapping("/reset")
    public Result<Void> resetSystemConfigs() {
        // 这里可以实现重置逻辑，比如先清空再初始化
        systemConfigService.initDefaultConfigs();
        return Result.success();
    }
} 