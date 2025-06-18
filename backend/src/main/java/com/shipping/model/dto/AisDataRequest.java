package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AIS数据请求DTO
 */
@Schema(description = "AIS数据请求DTO")
public class AisDataRequest {

    @Schema(description = "MMSI", example = "413123456")
    @NotBlank(message = "MMSI不能为空")
    @Size(max = 20, message = "MMSI长度不能超过20字符")
    private String mmsi;

    @Schema(description = "船舶ID", example = "1")
    private Long shipId;

    @Schema(description = "经度", example = "121.4648")
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "31.2304")
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    private BigDecimal latitude;

    @Schema(description = "航速（节）", example = "12.5")
    @DecimalMin(value = "0.0", message = "航速不能为负数")
    @DecimalMax(value = "50.0", message = "航速不能超过50节")
    private BigDecimal speed;

    @Schema(description = "航向（度）", example = "180.0")
    @DecimalMin(value = "0.0", message = "航向必须在0到360度之间")
    @DecimalMax(value = "360.0", message = "航向必须在0到360度之间")
    private BigDecimal heading;

    @Schema(description = "时间戳", example = "2024-01-15 08:00:00")
    @NotNull(message = "时间戳不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Constructors
    public AisDataRequest() {}

    // Getters and Setters
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

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getHeading() {
        return heading;
    }

    public void setHeading(BigDecimal heading) {
        this.heading = heading;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 