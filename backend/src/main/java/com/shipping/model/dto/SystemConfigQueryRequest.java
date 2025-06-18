package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 系统配置查询请求DTO
 */
@Schema(description = "系统配置查询请求DTO")
public class SystemConfigQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "配置键（模糊查询）", example = "system")
    private String configKey;

    @Schema(description = "配置值（模糊查询）", example = "华东")
    private String configValue;

    @Schema(description = "描述（模糊查询）", example = "公司")
    private String description;

    @Schema(description = "排序字段", example = "config_key")
    private String sortField = "config_key";

    @Schema(description = "排序方向", example = "ASC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "ASC";

    // Constructors
    public SystemConfigQueryRequest() {}

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

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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