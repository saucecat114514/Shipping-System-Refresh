package com.shipping.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Schema(description = "订单")
public class Order {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNumber;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "航次ID")
    private Long voyageId;

    @Schema(description = "起始港口ID")
    private Long originPortId;

    @Schema(description = "目的港口ID")
    private Long destinationPortId;

    @Schema(description = "货物名称")
    private String cargoName;

    @Schema(description = "货物类型")
    private String cargoType;

    @Schema(description = "货物重量（吨）")
    private BigDecimal cargoWeight;

    @Schema(description = "货物体积（立方米）")
    private BigDecimal cargoVolume;

    @Schema(description = "是否加急")
    private Boolean isUrgent;

    @Schema(description = "基础运价")
    private BigDecimal basePrice;

    @Schema(description = "附加费用")
    private BigDecimal additionalFees;

    @Schema(description = "总运费")
    private BigDecimal totalPrice;

    @Schema(description = "订单状态")
    private String status; // PENDING, CONFIRMED, IN_TRANSIT, DELIVERED, CANCELLED

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    // 关联对象
    @Schema(description = "客户信息")
    private User customer;

    @Schema(description = "航次信息")
    private Voyage voyage;

    // 扩展字段（用于显示）
    @Schema(description = "船舶名称")
    private String shipName;

    @Schema(description = "起始港口名称")
    private String originPortName;

    @Schema(description = "目的港口名称")
    private String destinationPortName;

    @Schema(description = "航线名称")
    private String routeName;

    public Order() {}

    public Order(String orderNumber, Long customerId, String cargoName, String cargoType, BigDecimal cargoWeight) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.cargoName = cargoName;
        this.cargoType = cargoType;
        this.cargoWeight = cargoWeight;
        this.isUrgent = false;
        this.status = "PENDING";
        this.additionalFees = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
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

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public BigDecimal getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(BigDecimal cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public BigDecimal getCargoVolume() {
        return cargoVolume;
    }

    public void setCargoVolume(BigDecimal cargoVolume) {
        this.cargoVolume = cargoVolume;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getAdditionalFees() {
        return additionalFees;
    }

    public void setAdditionalFees(BigDecimal additionalFees) {
        this.additionalFees = additionalFees;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getOriginPortName() {
        return originPortName;
    }

    public void setOriginPortName(String originPortName) {
        this.originPortName = originPortName;
    }

    public String getDestinationPortName() {
        return destinationPortName;
    }

    public void setDestinationPortName(String destinationPortName) {
        this.destinationPortName = destinationPortName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", cargoName='" + cargoName + '\'' +
                ", cargoType='" + cargoType + '\'' +
                ", cargoWeight=" + cargoWeight +
                ", status='" + status + '\'' +
                '}';
    }
} 