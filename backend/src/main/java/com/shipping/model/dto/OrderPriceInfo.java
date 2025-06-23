package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * 订单价格信息DTO
 */
@Schema(description = "订单价格信息")
public class OrderPriceInfo {

    @Schema(description = "基础运价", example = "2500.00")
    private BigDecimal basePrice;

    @Schema(description = "附加费用", example = "300.00")
    private BigDecimal additionalFees;

    @Schema(description = "总价格", example = "2800.00")
    private BigDecimal totalPrice;

    public OrderPriceInfo() {}

    public OrderPriceInfo(BigDecimal basePrice, BigDecimal additionalFees, BigDecimal totalPrice) {
        this.basePrice = basePrice;
        this.additionalFees = additionalFees;
        this.totalPrice = totalPrice;
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

    @Override
    public String toString() {
        return "OrderPriceInfo{" +
                "basePrice=" + basePrice +
                ", additionalFees=" + additionalFees +
                ", totalPrice=" + totalPrice +
                '}';
    }
} 