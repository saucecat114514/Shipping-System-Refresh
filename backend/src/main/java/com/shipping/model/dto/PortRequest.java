package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 港口请求DTO
 */
@Schema(description = "港口请求DTO")
public class PortRequest {

    @Schema(description = "港口名称（中文）", example = "上海港")
    @NotBlank(message = "港口中文名称不能为空")
    @Size(max = 100, message = "港口中文名称长度不能超过100字符")
    private String nameCn;

    @Schema(description = "港口名称（英文）", example = "Port of Shanghai")
    @NotBlank(message = "港口英文名称不能为空")
    @Size(max = 100, message = "港口英文名称长度不能超过100字符")
    private String nameEn;

    @Schema(description = "港口代码（UN/LOCODE）", example = "CNSHA")
    @NotBlank(message = "港口代码不能为空")
    @Size(max = 20, message = "港口代码长度不能超过20字符")
    private String code;

    @Schema(description = "所属国家", example = "中国")
    @NotBlank(message = "所属国家不能为空")
    @Size(max = 50, message = "国家名称长度不能超过50字符")
    private String country;

    @Schema(description = "经度", example = "121.4737")
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "31.2304")
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    // Constructors
    public PortRequest() {}

    public PortRequest(String nameCn, String nameEn, String code, String country, 
                       BigDecimal longitude, BigDecimal latitude) {
        this.nameCn = nameCn;
        this.nameEn = nameEn;
        this.code = code;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and Setters
    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
} 