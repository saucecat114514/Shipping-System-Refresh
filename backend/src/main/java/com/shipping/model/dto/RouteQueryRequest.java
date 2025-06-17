package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 航线查询请求DTO
 */
@Schema(description = "航线查询请求DTO")
public class RouteQueryRequest {

    @Schema(description = "页码，从1开始", example = "1")
    private int page = 1;

    @Schema(description = "每页大小", example = "10")
    private int size = 10;

    @Schema(description = "航线编号", example = "R001")
    private String routeNumber;

    @Schema(description = "航线名称（支持模糊查询）", example = "上海")
    private String name;

    @Schema(description = "起始港口ID", example = "1")
    private Long originPortId;

    @Schema(description = "目的港口ID", example = "2")
    private Long destinationPortId;

    @Schema(description = "航线状态：0-停用，1-启用", example = "1")
    private Integer status;

    // Constructors
    public RouteQueryRequest() {}

    // Getters and Setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取分页偏移量
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
} 