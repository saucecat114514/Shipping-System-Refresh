package com.shipping.controller;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import com.shipping.model.entity.Ship;
import com.shipping.model.dto.ShipRequest;
import com.shipping.model.dto.ShipQueryRequest;
import com.shipping.service.ShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 船舶管理控制器
 * 管理员和调度员可以管理船舶，客户只能查看
 */
@Tag(name = "船舶管理", description = "船舶信息的增删改查操作")
@RestController
@RequestMapping("/ships")
public class ShipController {

    @Autowired
    private ShipService shipService;

    /**
     * 创建船舶
     */
    @Operation(summary = "创建船舶", description = "添加新的船舶信息")
    @PostMapping
    public Result<Ship> createShip(@Valid @RequestBody ShipRequest shipRequest) {
        return shipService.createShip(shipRequest);
    }

    /**
     * 删除船舶
     */
    @Operation(summary = "删除船舶", description = "根据ID删除船舶")
    @DeleteMapping("/{id}")
    public Result<Void> deleteShip(
            @Parameter(description = "船舶ID") @PathVariable Long id) {
        return shipService.deleteShip(id);
    }

    /**
     * 更新船舶信息
     */
    @Operation(summary = "更新船舶", description = "更新船舶信息")
    @PutMapping("/{id}")
    public Result<Ship> updateShip(
            @Parameter(description = "船舶ID") @PathVariable Long id,
            @Valid @RequestBody ShipRequest shipRequest) {
        return shipService.updateShip(id, shipRequest);
    }

    /**
     * 根据ID查询船舶
     */
    @Operation(summary = "查询船舶详情", description = "根据ID查询船舶详细信息")
    @GetMapping("/{id}")
    public Result<Ship> getShipById(
            @Parameter(description = "船舶ID") @PathVariable Long id) {
        return shipService.getShipById(id);
    }

    /**
     * 根据MMSI查询船舶
     */
    @Operation(summary = "根据MMSI查询船舶", description = "根据MMSI查询船舶信息")
    @GetMapping("/mmsi/{mmsi}")
    public Result<Ship> getShipByMmsi(
            @Parameter(description = "MMSI") @PathVariable String mmsi) {
        return shipService.getShipByMmsi(mmsi);
    }

    /**
     * 根据IMO编号查询船舶
     */
    @Operation(summary = "根据IMO编号查询船舶", description = "根据IMO编号查询船舶信息")
    @GetMapping("/imo/{imoNumber}")
    public Result<Ship> getShipByImoNumber(
            @Parameter(description = "IMO编号") @PathVariable String imoNumber) {
        return shipService.getShipByImoNumber(imoNumber);
    }

    /**
     * 分页查询船舶列表
     */
    @Operation(summary = "分页查询船舶", description = "分页查询船舶列表，支持按名称、类型、船籍等筛选")
    @GetMapping
    public Result<PageResult<Ship>> getShipPage(ShipQueryRequest queryRequest) {
        return shipService.getShipPage(queryRequest);
    }

    /**
     * 获取所有船舶列表
     */
    @Operation(summary = "获取所有船舶", description = "获取所有船舶列表，用于下拉框等")
    @GetMapping("/all")
    public Result<List<Ship>> getAllShips() {
        return shipService.getAllShips();
    }
} 