package com.shipping.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AIS数据实体类
 */
@Schema(description = "AIS数据")
public class AisData {

    @Schema(description = "AIS数据ID")
    private Long id;

    @Schema(description = "MMSI")
    private String mmsi;

    @Schema(description = "船舶ID")
    private Long shipId;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "航速（节）")
    private BigDecimal speed;

    @Schema(description = "航向（度）")
    private BigDecimal heading;

    @Schema(description = "时间戳")
    private LocalDateTime timestamp;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    // 关联对象
    @Schema(description = "船舶信息")
    private Ship ship;

    public AisData() {}

    public AisData(String mmsi, Long shipId, BigDecimal longitude, BigDecimal latitude, LocalDateTime timestamp) {
        this.mmsi = mmsi;
        this.shipId = shipId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    @Override
    public String toString() {
        return "AisData{" +
                "id=" + id +
                ", mmsi='" + mmsi + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", speed=" + speed +
                ", timestamp=" + timestamp +
                '}';
    }
} 