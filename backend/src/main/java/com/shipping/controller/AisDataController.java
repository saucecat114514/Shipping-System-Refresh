package com.shipping.controller;

import com.shipping.service.AisDataService;
import com.shipping.model.entity.AisData;
import com.shipping.model.dto.AisDataRequest;
import com.shipping.model.dto.AisDataQueryRequest;
import com.shipping.common.Result;
import com.shipping.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * AIS数据管理控制器
 */
@Tag(name = "AIS数据管理", description = "AIS数据管理相关接口")
@RestController
@RequestMapping("/ais-data")
public class AisDataController {

    @Autowired
    private AisDataService aisDataService;

    @Operation(summary = "添加AIS数据", description = "添加单条AIS数据记录")
    @PostMapping
    public Result<Void> addAisData(@Valid @RequestBody AisDataRequest request) {
        aisDataService.addAisData(request);
        return Result.success();
    }

    @Operation(summary = "批量添加AIS数据", description = "批量添加AIS数据记录")
    @PostMapping("/batch")
    public Result<Void> addAisDataBatch(@Valid @RequestBody List<AisDataRequest> requests) {
        aisDataService.addAisDataBatch(requests);
        return Result.success();
    }

    @Operation(summary = "根据ID查询AIS数据", description = "根据ID查询AIS数据详情")
    @GetMapping("/{id}")
    public Result<AisData> getAisDataById(@Parameter(description = "AIS数据ID") @PathVariable Long id) {
        AisData aisData = aisDataService.getAisDataById(id);
        return Result.success(aisData);
    }

    @Operation(summary = "分页查询AIS数据", description = "分页查询AIS数据列表")
    @GetMapping
    public Result<PageResult<AisData>> getAisDataPage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "MMSI") @RequestParam(required = false) String mmsi,
            @Parameter(description = "船舶ID") @RequestParam(required = false) Long shipId,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "最小经度") @RequestParam(required = false) Double minLongitude,
            @Parameter(description = "最大经度") @RequestParam(required = false) Double maxLongitude,
            @Parameter(description = "最小纬度") @RequestParam(required = false) Double minLatitude,
            @Parameter(description = "最大纬度") @RequestParam(required = false) Double maxLatitude,
            @Parameter(description = "最小航速") @RequestParam(required = false) Double minSpeed,
            @Parameter(description = "最大航速") @RequestParam(required = false) Double maxSpeed,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "timestamp") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        AisDataQueryRequest query = new AisDataQueryRequest();
        query.setPage(page);
        query.setSize(size);
        query.setMmsi(mmsi);
        query.setShipId(shipId);
        // 注意：实际使用中应该解析时间字符串为LocalDateTime
        // query.setStartTime(startTime != null ? LocalDateTime.parse(startTime) : null);
        // query.setEndTime(endTime != null ? LocalDateTime.parse(endTime) : null);
        query.setSortField(sortField);
        query.setSortDirection(sortDirection);
        
        PageResult<AisData> result = aisDataService.getAisDataPage(query);
        return Result.success(result);
    }

    @Operation(summary = "查询最新AIS数据", description = "根据MMSI查询最新的AIS数据")
    @GetMapping("/latest/mmsi/{mmsi}")
    public Result<AisData> getLatestAisDataByMmsi(@Parameter(description = "MMSI") @PathVariable String mmsi) {
        AisData aisData = aisDataService.getLatestAisDataByMmsi(mmsi);
        return Result.success(aisData);
    }

    @Operation(summary = "查询船舶最新位置", description = "根据船舶ID查询最新的AIS数据")
    @GetMapping("/latest/ship/{shipId}")
    public Result<AisData> getLatestAisDataByShipId(@Parameter(description = "船舶ID") @PathVariable Long shipId) {
        AisData aisData = aisDataService.getLatestAisDataByShipId(shipId);
        return Result.success(aisData);
    }

    @Operation(summary = "查询船舶轨迹", description = "根据船舶ID查询指定时间范围内的轨迹数据")
    @GetMapping("/track/ship/{shipId}")
    public Result<List<AisData>> getShipTrack(
            @Parameter(description = "船舶ID") @PathVariable Long shipId,
            @Parameter(description = "开始时间", example = "2024-01-01 00:00:00") @RequestParam String startTime,
            @Parameter(description = "结束时间", example = "2024-01-31 23:59:59") @RequestParam String endTime) {
        List<AisData> trackData = aisDataService.getShipTrack(shipId, startTime, endTime);
        return Result.success(trackData);
    }

    @Operation(summary = "根据MMSI查询轨迹", description = "根据MMSI查询指定时间范围内的轨迹数据")
    @GetMapping("/track/mmsi/{mmsi}")
    public Result<List<AisData>> getShipTrackByMmsi(
            @Parameter(description = "MMSI") @PathVariable String mmsi,
            @Parameter(description = "开始时间", example = "2024-01-01 00:00:00") @RequestParam String startTime,
            @Parameter(description = "结束时间", example = "2024-01-31 23:59:59") @RequestParam String endTime) {
        List<AisData> trackData = aisDataService.getShipTrackByMmsi(mmsi, startTime, endTime);
        return Result.success(trackData);
    }

    @Operation(summary = "获取实时位置", description = "获取多个船舶的实时位置")
    @PostMapping("/real-time-positions")
    public Result<List<AisData>> getRealTimePositions(@RequestBody List<Long> shipIds) {
        List<AisData> positions = aisDataService.getRealTimePositions(shipIds);
        return Result.success(positions);
    }

    @Operation(summary = "删除AIS数据", description = "根据ID删除AIS数据")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAisData(@Parameter(description = "AIS数据ID") @PathVariable Long id) {
        aisDataService.deleteAisData(id);
        return Result.success();
    }

    @Operation(summary = "批量删除AIS数据", description = "批量删除AIS数据")
    @DeleteMapping("/batch")
    public Result<Void> deleteAisDataBatch(@RequestBody List<Long> ids) {
        aisDataService.deleteAisDataBatch(ids);
        return Result.success();
    }

    @Operation(summary = "清理过期数据", description = "清理指定天数之前的AIS数据")
    @DeleteMapping("/clean-expired")
    public Result<Void> cleanExpiredData(
            @Parameter(description = "保留天数", example = "30") @RequestParam(defaultValue = "30") int daysBefore) {
        aisDataService.cleanExpiredData(daysBefore);
        return Result.success();
    }
} 