package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 船舶查询请求DTO
 */
@Schema(description = "船舶查询请求DTO")
public class ShipQueryRequest {

    @Schema(description = "页码，从1开始", example = "1")
    private int page = 1;

    @Schema(description = "每页大小", example = "10")
    private int size = 10;

    @Schema(description = "船舶名称（支持模糊查询）", example = "MSC")
    private String name;

    @Schema(description = "船舶类型（中文或英文，支持模糊查询）", example = "集装箱")
    private String type;

    @Schema(description = "船籍", example = "巴拿马")
    private String flag;

    @Schema(description = "MMSI", example = "636012345")
    private String mmsi;

    @Schema(description = "IMO编号", example = "IMO1234567")
    private String imoNumber;

    @Schema(description = "船舶状态：0-停泊，1-航行中，2-锚泊，3-维修", example = "1")
    private Integer status;

    // Constructors
    public ShipQueryRequest() {}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取分页偏移量
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
} 