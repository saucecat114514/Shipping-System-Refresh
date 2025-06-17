package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 港口查询请求DTO
 */
@Schema(description = "港口查询请求DTO")
public class PortQueryRequest {

    @Schema(description = "页码，从1开始", example = "1")
    private int page = 1;

    @Schema(description = "每页大小", example = "10")
    private int size = 10;

    @Schema(description = "港口名称（中文或英文，支持模糊查询）", example = "上海")
    private String name;

    @Schema(description = "港口代码", example = "CNSHA")
    private String code;

    @Schema(description = "所属国家", example = "中国")
    private String country;

    // Constructors
    public PortQueryRequest() {}

    public PortQueryRequest(int page, int size, String name, String code, String country) {
        this.page = page;
        this.size = size;
        this.name = name;
        this.code = code;
        this.country = country;
    }

    // Getters and Setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * 获取分页偏移量
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
} 