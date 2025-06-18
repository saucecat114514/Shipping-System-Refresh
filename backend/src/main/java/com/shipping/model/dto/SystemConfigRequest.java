package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 系统配置请求DTO
 */
@Schema(description = "系统配置请求DTO")
public class SystemConfigRequest {

    @Schema(description = "配置键", example = "system.company.name")
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100字符")
    private String configKey;

    @Schema(description = "配置值", example = "华东航运有限公司")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 1000, message = "配置值长度不能超过1000字符")
    private String configValue;

    @Schema(description = "描述", example = "公司名称配置")
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    // Constructors
    public SystemConfigRequest() {}

    public SystemConfigRequest(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
    }

    // Getters and Setters
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
} 