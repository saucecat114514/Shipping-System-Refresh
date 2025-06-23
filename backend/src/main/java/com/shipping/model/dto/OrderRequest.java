package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 订单请求DTO
 */
@Schema(description = "订单请求DTO")
public class OrderRequest {

    @Schema(description = "订单编号", example = "ORD202401001")
    @NotBlank(message = "订单编号不能为空")
    @Size(max = 20, message = "订单编号长度不能超过20字符")
    private String orderNumber;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "航次ID（可选，订单确认时分配）", example = "1")
    private Long voyageId;

    @Schema(description = "货物名称", example = "集装箱货物")
    @NotBlank(message = "货物名称不能为空")
    @Size(max = 100, message = "货物名称长度不能超过100字符")
    private String cargoName;

    @Schema(description = "货物类型", example = "普通货物", allowableValues = {"普通货物", "危险品", "冷藏货物", "液体货物", "散装货物"})
    @NotBlank(message = "货物类型不能为空")
    @Size(max = 20, message = "货物类型长度不能超过20字符")
    private String cargoType;

    @Schema(description = "货物重量（吨）", example = "25.5")
    @NotNull(message = "货物重量不能为空")
    @DecimalMin(value = "0.1", message = "货物重量必须大于0.1吨")
    private BigDecimal cargoWeight;

    @Schema(description = "货物体积（立方米）", example = "50.0")
    @DecimalMin(value = "0.1", message = "货物体积必须大于0.1立方米")
    private BigDecimal cargoVolume;

    @Schema(description = "是否加急", example = "false")
    private Boolean isUrgent = false;

    @Schema(description = "基础运价", example = "5000.00")
    private BigDecimal basePrice;

    @Schema(description = "附加费用", example = "500.00")
    private BigDecimal additionalFees = BigDecimal.ZERO;

    @Schema(description = "总运费", example = "5500.00")
    private BigDecimal totalPrice;

    @Schema(description = "订单状态", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "IN_TRANSIT", "DELIVERED", "CANCELLED"})
    @Size(max = 20, message = "订单状态长度不能超过20字符")
    private String status = "PENDING";

    @Schema(description = "备注", example = "特殊处理要求")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String notes;

    // Constructors
    public OrderRequest() {}

    // Getters and Setters
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
} 