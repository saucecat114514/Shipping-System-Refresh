package com.shipping.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 航次实体类
 */
@Schema(description = "航次")
public class Voyage {

    @Schema(description = "航次ID")
    private Long id;

    @Schema(description = "航次编号")
    private String voyageNumber;

    @Schema(description = "航线ID")
    private Long routeId;

    @Schema(description = "船舶ID")
    private Long shipId;

    @Schema(description = "计划出发日期")
    private LocalDateTime departureDate;

    @Schema(description = "计划到达日期")
    private LocalDateTime arrivalDate;

    @Schema(description = "实际出发日期")
    private LocalDateTime actualDepartureDate;

    @Schema(description = "实际到达日期")
    private LocalDateTime actualArrivalDate;

    @Schema(description = "航次状态")
    private String status; // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    // 关联对象
    @Schema(description = "航线信息")
    private Route route;

    @Schema(description = "船舶信息")
    private Ship ship;

    public Voyage() {}

    public Voyage(String voyageNumber, Long routeId, Long shipId, LocalDateTime departureDate, LocalDateTime arrivalDate) {
        this.voyageNumber = voyageNumber;
        this.routeId = routeId;
        this.shipId = shipId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = "PLANNED";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDateTime getActualDepartureDate() {
        return actualDepartureDate;
    }

    public void setActualDepartureDate(LocalDateTime actualDepartureDate) {
        this.actualDepartureDate = actualDepartureDate;
    }

    public LocalDateTime getActualArrivalDate() {
        return actualArrivalDate;
    }

    public void setActualArrivalDate(LocalDateTime actualArrivalDate) {
        this.actualArrivalDate = actualArrivalDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    @Override
    public String toString() {
        return "Voyage{" +
                "id=" + id +
                ", voyageNumber='" + voyageNumber + '\'' +
                ", routeId=" + routeId +
                ", shipId=" + shipId +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                ", status='" + status + '\'' +
                '}';
    }
} 