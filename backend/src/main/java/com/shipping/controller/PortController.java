package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Port;
import com.shipping.model.dto.PortRequest;
import com.shipping.model.dto.PortQueryRequest;
import com.shipping.service.PortService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 港口管理控制器
 */
@Tag(name = "港口管理", description = "港口信息的增删改查操作")
@RestController
@RequestMapping("/api/ports")
public class PortController {

    @Autowired
    private PortService portService;

    /**
     * 创建港口
     */
    @Operation(summary = "创建港口", description = "添加新的港口信息")
    @PostMapping
    public Result<Port> createPort(@Valid @RequestBody PortRequest portRequest) {
        return portService.createPort(portRequest);
    }

    /**
     * 删除港口
     */
    @Operation(summary = "删除港口", description = "根据ID删除港口")
    @DeleteMapping("/{id}")
    public Result<Void> deletePort(
            @Parameter(description = "港口ID") @PathVariable Long id) {
        return portService.deletePort(id);
    }

    /**
     * 更新港口信息
     */
    @Operation(summary = "更新港口", description = "更新港口信息")
    @PutMapping("/{id}")
    public Result<Port> updatePort(
            @Parameter(description = "港口ID") @PathVariable Long id,
            @Valid @RequestBody PortRequest portRequest) {
        return portService.updatePort(id, portRequest);
    }

    /**
     * 根据ID查询港口
     */
    @Operation(summary = "查询港口详情", description = "根据ID查询港口详细信息")
    @GetMapping("/{id}")
    public Result<Port> getPortById(
            @Parameter(description = "港口ID") @PathVariable Long id) {
        return portService.getPortById(id);
    }

    /**
     * 根据代码查询港口
     */
    @Operation(summary = "根据代码查询港口", description = "根据港口代码查询港口信息")
    @GetMapping("/code/{code}")
    public Result<Port> getPortByCode(
            @Parameter(description = "港口代码") @PathVariable String code) {
        return portService.getPortByCode(code);
    }

    /**
     * 分页查询港口列表
     */
    @Operation(summary = "分页查询港口", description = "分页查询港口列表，支持按名称、代码、国家筛选")
    @GetMapping
    public Result<PageResult<Port>> getPortPage(PortQueryRequest queryRequest) {
        return portService.getPortPage(queryRequest);
    }

    /**
     * 获取所有港口列表
     */
    @Operation(summary = "获取所有港口", description = "获取所有港口列表，用于下拉框等")
    @GetMapping("/all")
    public Result<List<Port>> getAllPorts() {
        return portService.getAllPorts();
    }
} 