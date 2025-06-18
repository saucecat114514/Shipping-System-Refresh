package com.shipping.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * 用户请求DTO
 */
@Schema(description = "用户请求DTO")
public class UserRequest {

    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和连字符")
    private String username;

    @Schema(description = "密码", example = "123456")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100字符之间")
    private String password;

    @Schema(description = "邮箱", example = "zhangsan@shipping.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "真实姓名", example = "张三")
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 100, message = "真实姓名长度不能超过100字符")
    private String realName;

    @Schema(description = "角色", example = "CUSTOMER", allowableValues = {"ADMIN", "DISPATCHER", "CUSTOMER"})
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(ADMIN|DISPATCHER|CUSTOMER)$", message = "角色必须是ADMIN、DISPATCHER或CUSTOMER")
    private String role;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status = 1;

    @Schema(description = "电话号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "部门", example = "运营部")
    private String department;

    @Schema(description = "职位", example = "调度员")
    private String position;

    @Schema(description = "备注", example = "VIP客户")
    private String remark;

    // Constructors
    public UserRequest() {}

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
} 