package com.shipping.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航线实体
 */
@Schema(description = "航线实体")
public class Route {

    @Schema(description = "航线ID")
    private Long id;

    @Schema(description = "航线编号")
    private String routeNumber;

    @Schema(description = "航线名称")
    private String name;

    @Schema(description = "起始港口ID")
    private Long originPortId;

    @Schema(description = "目的港口ID")
    private Long destinationPortId;

    @Schema(description = "航程距离（公里）")
    private BigDecimal distance;

    @Schema(description = "航程距离（海里）")
    private BigDecimal distanceNm;

    @Schema(description = "预计航行时间（小时）")
    private BigDecimal estimatedDuration;

    @Schema(description = "航线状态：0-停用，1-启用")
    private Integer status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    // 关联实体字段（非数据库字段）
    @Schema(description = "起始港口信息")
    private Port originPort;

    @Schema(description = "目的港口信息")
    private Port destinationPort;

    // Constructors
    public Route() {}

    public Route(String routeNumber, String name, Long originPortId, 
                 Long destinationPortId, BigDecimal distance) {
        this.routeNumber = routeNumber;
        this.name = name;
        this.originPortId = originPortId;
        this.destinationPortId = destinationPortId;
        this.distance = distance;
        // 1海里 = 1.852公里
        this.distanceNm = distance.divide(new BigDecimal("1.852"), 2, BigDecimal.ROUND_HALF_UP);
        this.status = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOriginPortId() {
        return originPortId;
    }

    public void setOriginPortId(Long originPortId) {
        this.originPortId = originPortId;
    }

    public Long getDestinationPortId() {
        return destinationPortId;
    }

    public void setDestinationPortId(Long destinationPortId) {
        this.destinationPortId = destinationPortId;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
        if (distance != null) {
            this.distanceNm = distance.divide(new BigDecimal("1.852"), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public BigDecimal getDistanceNm() {
        return distanceNm;
    }

    public void setDistanceNm(BigDecimal distanceNm) {
        this.distanceNm = distanceNm;
    }

    public BigDecimal getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(BigDecimal estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Port getOriginPort() {
        return originPort;
    }

    public void setOriginPort(Port originPort) {
        this.originPort = originPort;
    }

    public Port getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(Port destinationPort) {
        this.destinationPort = destinationPort;
    }
} 