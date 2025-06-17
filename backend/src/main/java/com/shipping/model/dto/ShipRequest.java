package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;

/**
 * 船舶请求DTO
 */
@Schema(description = "船舶请求DTO")
public class ShipRequest {

    @Schema(description = "船舶名称", example = "MSC Oscar")
    @NotBlank(message = "船舶名称不能为空")
    @Size(max = 100, message = "船舶名称长度不能超过100字符")
    private String name;

    @Schema(description = "船舶类型（中文）", example = "集装箱船")
    @NotBlank(message = "船舶类型（中文）不能为空")
    @Size(max = 50, message = "船舶类型（中文）长度不能超过50字符")
    private String typeCn;

    @Schema(description = "船舶类型（英文）", example = "Container Ship")
    @NotBlank(message = "船舶类型（英文）不能为空")
    @Size(max = 50, message = "船舶类型（英文）长度不能超过50字符")
    private String typeEn;

    @Schema(description = "船籍", example = "巴拿马")
    @NotBlank(message = "船籍不能为空")
    @Size(max = 50, message = "船籍长度不能超过50字符")
    private String flag;

    @Schema(description = "MMSI（海事移动业务标识）", example = "636012345")
    @Size(max = 20, message = "MMSI长度不能超过20字符")
    private String mmsi;

    @Schema(description = "IMO编号", example = "IMO1234567")
    @Size(max = 20, message = "IMO编号长度不能超过20字符")
    private String imoNumber;

    @Schema(description = "总吨位（GRT）", example = "175000.50")
    private BigDecimal grossTonnage;

    @Schema(description = "载重吨位（DWT）", example = "190000.00")
    private BigDecimal deadweightTonnage;

    @Schema(description = "当前航速（节）", example = "12.5")
    @Min(value = 0, message = "航速不能为负数")
    @Max(value = 50, message = "航速不能超过50节")
    private BigDecimal currentSpeed;

    @Schema(description = "当前航向（度）", example = "285.0")
    @Min(value = 0, message = "航向必须在0-360度之间")
    @Max(value = 360, message = "航向必须在0-360度之间")
    private BigDecimal currentHeading;

    @Schema(description = "当前经度", example = "121.4737")
    private BigDecimal currentLongitude;

    @Schema(description = "当前纬度", example = "31.2304")
    private BigDecimal currentLatitude;

    @Schema(description = "船舶状态：0-停泊，1-航行中，2-锚泊，3-维修", example = "1")
    @NotNull(message = "船舶状态不能为空")
    @Min(value = 0, message = "船舶状态值必须在0-3之间")
    @Max(value = 3, message = "船舶状态值必须在0-3之间")
    private Integer status;

    // Constructors
    public ShipRequest() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCn() {
        return typeCn;
    }

    public void setTypeCn(String typeCn) {
        this.typeCn = typeCn;
    }

    public String getTypeEn() {
        return typeEn;
    }

    public void setTypeEn(String typeEn) {
        this.typeEn = typeEn;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getImoNumber() {
        return imoNumber;
    }

    public void setImoNumber(String imoNumber) {
        this.imoNumber = imoNumber;
    }

    public BigDecimal getGrossTonnage() {
        return grossTonnage;
    }

    public void setGrossTonnage(BigDecimal grossTonnage) {
        this.grossTonnage = grossTonnage;
    }

    public BigDecimal getDeadweightTonnage() {
        return deadweightTonnage;
    }

    public void setDeadweightTonnage(BigDecimal deadweightTonnage) {
        this.deadweightTonnage = deadweightTonnage;
    }

    public BigDecimal getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(BigDecimal currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public BigDecimal getCurrentHeading() {
        return currentHeading;
    }

    public void setCurrentHeading(BigDecimal currentHeading) {
        this.currentHeading = currentHeading;
    }

    public BigDecimal getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(BigDecimal currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public BigDecimal getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(BigDecimal currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
} 