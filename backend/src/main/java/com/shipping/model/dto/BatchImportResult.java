package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 批量导入结果DTO
 */
@Schema(description = "批量导入结果")
public class BatchImportResult {

    @Schema(description = "导入ID")
    private String importId;

    @Schema(description = "导入类型")
    private String importType;

    @Schema(description = "总行数")
    private Integer totalRows;

    @Schema(description = "成功行数")
    private Integer successRows;

    @Schema(description = "失败行数")
    private Integer failedRows;

    @Schema(description = "跳过行数")
    private Integer skippedRows;

    @Schema(description = "处理状态")
    private String status; // PROCESSING, SUCCESS, FAILED, PARTIAL_SUCCESS

    @Schema(description = "错误详情")
    private List<ImportError> errors;

    @Schema(description = "处理时间(毫秒)")
    private Long processingTime;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "操作用户")
    private String username;

    @Schema(description = "备注")
    private String remark;

    // 内部类 - 导入错误
    @Schema(description = "导入错误")
    public static class ImportError {
        @Schema(description = "行号")
        private Integer rowNumber;

        @Schema(description = "错误字段")
        private String field;

        @Schema(description = "错误信息")
        private String message;

        @Schema(description = "原始数据")
        private Map<String, Object> originalData;

        // Constructors
        public ImportError() {}

        public ImportError(Integer rowNumber, String field, String message, Map<String, Object> originalData) {
            this.rowNumber = rowNumber;
            this.field = field;
            this.message = message;
            this.originalData = originalData;
        }

        // Getters and Setters
        public Integer getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(Integer rowNumber) {
            this.rowNumber = rowNumber;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getOriginalData() {
            return originalData;
        }

        public void setOriginalData(Map<String, Object> originalData) {
            this.originalData = originalData;
        }
    }

    // Constructors
    public BatchImportResult() {}

    // Getters and Setters
    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getSuccessRows() {
        return successRows;
    }

    public void setSuccessRows(Integer successRows) {
        this.successRows = successRows;
    }

    public Integer getFailedRows() {
        return failedRows;
    }

    public void setFailedRows(Integer failedRows) {
        this.failedRows = failedRows;
    }

    public Integer getSkippedRows() {
        return skippedRows;
    }

    public void setSkippedRows(Integer skippedRows) {
        this.skippedRows = skippedRows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
} 