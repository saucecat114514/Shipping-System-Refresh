package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 航次请求DTO
 */
@Schema(description = "航次请求DTO")
public class VoyageRequest {

    @Schema(description = "航次编号", example = "V202401001")
    @NotBlank(message = "航次编号不能为空")
    @Size(max = 20, message = "航次编号长度不能超过20字符")
    private String voyageNumber;

    @Schema(description = "航线ID", example = "1")
    @NotNull(message = "航线ID不能为空")
    private Long routeId;

    @Schema(description = "船舶ID", example = "1")
    @NotNull(message = "船舶ID不能为空")
    private Long shipId;

    @Schema(description = "计划出发日期", example = "2024-01-15 08:00:00")
    @NotNull(message = "计划出发日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureDate;

    @Schema(description = "计划到达日期", example = "2024-01-25 18:00:00")
    @NotNull(message = "计划到达日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalDate;

    @Schema(description = "实际出发日期", example = "2024-01-15 08:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDepartureDate;

    @Schema(description = "实际到达日期", example = "2024-01-25 17:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualArrivalDate;

    @Schema(description = "航次状态", example = "PLANNED", allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"})
    @Size(max = 20, message = "航次状态长度不能超过20字符")
    private String status;

    // Constructors
    public VoyageRequest() {}

    // Getters and Setters
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
} 