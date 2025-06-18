package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 路径计算请求DTO
 */
@Schema(description = "路径计算请求DTO")
public class RouteCalculationRequest {

    @Schema(description = "起点")
    @NotNull(message = "起点不能为空")
    @Valid
    private LocationRequest startPoint;

    @Schema(description = "终点")
    @NotNull(message = "终点不能为空")
    @Valid
    private LocationRequest endPoint;

    @Schema(description = "途经点")
    @Valid
    private List<LocationRequest> waypoints;

    @Schema(description = "路径类型", example = "fastest", allowableValues = {"fastest", "shortest", "economic"})
    private String routeType = "fastest";

    @Schema(description = "是否避开收费站", example = "false")
    private Boolean avoidTolls = false;

    @Schema(description = "是否避开高速公路", example = "false")
    private Boolean avoidHighways = false;

    @Schema(description = "车辆类型", example = "car", allowableValues = {"car", "truck", "ship"})
    private String vehicleType = "ship";

    @Schema(description = "最大载重(吨)", example = "10000")
    private Double maxWeight;

    @Schema(description = "船舶类型", example = "container", allowableValues = {"container", "bulk", "tanker", "general"})
    private String shipType;

    // Constructors
    public RouteCalculationRequest() {}

    // Getters and Setters
    public LocationRequest getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LocationRequest startPoint) {
        this.startPoint = startPoint;
    }

    public LocationRequest getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LocationRequest endPoint) {
        this.endPoint = endPoint;
    }

    public List<LocationRequest> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<LocationRequest> waypoints) {
        this.waypoints = waypoints;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public Boolean getAvoidTolls() {
        return avoidTolls;
    }

    public void setAvoidTolls(Boolean avoidTolls) {
        this.avoidTolls = avoidTolls;
    }

    public Boolean getAvoidHighways() {
        return avoidHighways;
    }

    public void setAvoidHighways(Boolean avoidHighways) {
        this.avoidHighways = avoidHighways;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }
} 