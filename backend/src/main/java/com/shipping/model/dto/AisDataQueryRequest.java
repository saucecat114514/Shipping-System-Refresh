package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AIS数据查询请求DTO
 */
@Schema(description = "AIS数据查询请求DTO")
public class AisDataQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "MMSI", example = "413123456")
    private String mmsi;

    @Schema(description = "船舶ID", example = "1")
    private Long shipId;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "最小经度（地理范围筛选）", example = "120.0")
    private BigDecimal minLongitude;

    @Schema(description = "最大经度（地理范围筛选）", example = "122.0")
    private BigDecimal maxLongitude;

    @Schema(description = "最小纬度（地理范围筛选）", example = "30.0")
    private BigDecimal minLatitude;

    @Schema(description = "最大纬度（地理范围筛选）", example = "32.0")
    private BigDecimal maxLatitude;

    @Schema(description = "最小航速筛选", example = "5.0")
    private BigDecimal minSpeed;

    @Schema(description = "最大航速筛选", example = "20.0")
    private BigDecimal maxSpeed;

    @Schema(description = "排序字段", example = "timestamp")
    private String sortField = "timestamp";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "DESC";

    // Constructors
    public AisDataQueryRequest() {}

    // Getters and Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(BigDecimal minLongitude) {
        this.minLongitude = minLongitude;
    }

    public BigDecimal getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(BigDecimal maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public BigDecimal getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(BigDecimal minLatitude) {
        this.minLatitude = minLatitude;
    }

    public BigDecimal getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(BigDecimal maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public BigDecimal getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(BigDecimal minSpeed) {
        this.minSpeed = minSpeed;
    }

    public BigDecimal getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(BigDecimal maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
} 