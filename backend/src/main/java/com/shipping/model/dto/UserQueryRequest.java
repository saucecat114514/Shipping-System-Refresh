package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户查询请求DTO
 */
@Schema(description = "用户查询请求DTO")
public class UserQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "用户名（模糊查询）", example = "zhang")
    private String username;

    @Schema(description = "邮箱（模糊查询）", example = "example.com")
    private String email;

    @Schema(description = "真实姓名（模糊查询）", example = "张")
    private String realName;

    @Schema(description = "角色", example = "CUSTOMER", allowableValues = {"ADMIN", "DISPATCHER", "CUSTOMER"})
    private String role;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "电话号码（模糊查询）", example = "138")
    private String phone;

    @Schema(description = "部门（模糊查询）", example = "运营")
    private String department;

    @Schema(description = "职位（模糊查询）", example = "调度")
    private String position;

    @Schema(description = "排序字段", example = "created_at")
    private String sortField = "created_at";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "DESC";

    // Constructors
    public UserQueryRequest() {}

    // Getters and Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
} 