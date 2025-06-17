package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 航线请求DTO
 */
@Schema(description = "航线请求DTO")
public class RouteRequest {

    @Schema(description = "航线编号", example = "R001")
    @NotBlank(message = "航线编号不能为空")
    @Size(max = 20, message = "航线编号长度不能超过20字符")
    private String routeNumber;

    @Schema(description = "航线名称", example = "上海-洛杉矶航线")
    @NotBlank(message = "航线名称不能为空")
    @Size(max = 100, message = "航线名称长度不能超过100字符")
    private String name;

    @Schema(description = "起始港口ID", example = "1")
    @NotNull(message = "起始港口ID不能为空")
    private Long originPortId;

    @Schema(description = "目的港口ID", example = "2")
    @NotNull(message = "目的港口ID不能为空")
    private Long destinationPortId;

    @Schema(description = "航程距离（公里）", example = "11000.50")
    @NotNull(message = "航程距离不能为空")
    @Min(value = 0, message = "航程距离不能为负数")
    private BigDecimal distance;

    @Schema(description = "预计航行时间（小时）", example = "240.0")
    @Min(value = 0, message = "预计航行时间不能为负数")
    private BigDecimal estimatedDuration;

    @Schema(description = "航线状态：0-停用，1-启用", example = "1")
    @NotNull(message = "航线状态不能为空")
    @Min(value = 0, message = "航线状态值必须在0-1之间")
    @Min(value = 1, message = "航线状态值必须在0-1之间")
    private Integer status;

    @Schema(description = "描述", example = "这是一条跨太平洋的主要航线")
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    // Constructors
    public RouteRequest() {}

    // Getters and Setters
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
} 