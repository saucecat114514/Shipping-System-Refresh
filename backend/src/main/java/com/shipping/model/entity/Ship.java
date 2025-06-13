package com.shipping.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 船舶实体
 */
@Schema(description = "船舶实体")
public class Ship {

    @Schema(description = "船舶ID")
    private Long id;

    @Schema(description = "船舶名称")
    private String name;

    @Schema(description = "船舶类型（中文）")
    private String typeCn;

    @Schema(description = "船舶类型（英文）")
    private String typeEn;

    @Schema(description = "船籍")
    private String flag;

    @Schema(description = "MMSI（海事移动业务标识）")
    private String mmsi;

    @Schema(description = "IMO编号")
    private String imoNumber;

    @Schema(description = "总吨位（GRT）")
    private BigDecimal grossTonnage;

    @Schema(description = "载重吨位（DWT）")
    private BigDecimal deadweightTonnage;

    @Schema(description = "当前航速（节）")
    private BigDecimal currentSpeed;

    @Schema(description = "当前航向（度）")
    private BigDecimal currentHeading;

    @Schema(description = "当前经度")
    private BigDecimal currentLongitude;

    @Schema(description = "当前纬度")
    private BigDecimal currentLatitude;

    @Schema(description = "船舶状态：0-停泊，1-航行中，2-锚泊，3-维修")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    // Constructors
    public Ship() {}

    public Ship(String name, String typeCn, String typeEn, String flag, 
                String mmsi, String imoNumber, BigDecimal grossTonnage, 
                BigDecimal deadweightTonnage) {
        this.name = name;
        this.typeCn = typeCn;
        this.typeEn = typeEn;
        this.flag = flag;
        this.mmsi = mmsi;
        this.imoNumber = imoNumber;
        this.grossTonnage = grossTonnage;
        this.deadweightTonnage = deadweightTonnage;
        this.status = 0;
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
} 