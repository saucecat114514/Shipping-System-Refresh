package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 批量导入请求DTO
 */
@Schema(description = "批量导入请求")
public class BatchImportRequest {

    @Schema(description = "导入类型", example = "SHIP", required = true)
    @NotBlank(message = "导入类型不能为空")
    private String importType;

    @Schema(description = "是否跳过错误行", example = "true")
    private Boolean skipErrors = true;

    @Schema(description = "覆盖模式", example = "APPEND")
    private String overrideMode = "APPEND"; // APPEND, UPDATE, REPLACE

    @Schema(description = "备注")
    private String remark;

    // Constructors
    public BatchImportRequest() {}

    // Getters and Setters
    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public Boolean getSkipErrors() {
        return skipErrors;
    }

    public void setSkipErrors(Boolean skipErrors) {
        this.skipErrors = skipErrors;
    }

    public String getOverrideMode() {
        return overrideMode;
    }

    public void setOverrideMode(String overrideMode) {
        this.overrideMode = overrideMode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
} 