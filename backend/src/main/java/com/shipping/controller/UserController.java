package com.shipping.controller;

import com.shipping.service.UserService;
import com.shipping.model.entity.User;
import com.shipping.model.dto.UserRequest;
import com.shipping.model.dto.UserQueryRequest;
import com.shipping.model.dto.ChangePasswordRequest;
import com.shipping.common.Result;
import com.shipping.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody UserRequest request) {
        userService.createUser(request);
        return Result.success();
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @Operation(summary = "根据ID查询用户", description = "根据用户ID查询用户详情")
    @GetMapping("/{id}")
    public Result<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return Result.success(user);
    }

    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    @GetMapping
    public Result<PageResult<User>> getUserPage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户名（模糊查询）") @RequestParam(required = false) String username,
            @Parameter(description = "邮箱（模糊查询）") @RequestParam(required = false) String email,
            @Parameter(description = "真实姓名（模糊查询）") @RequestParam(required = false) String realName,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "用户状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "电话号码（模糊查询）") @RequestParam(required = false) String phone,
            @Parameter(description = "部门（模糊查询）") @RequestParam(required = false) String department,
            @Parameter(description = "职位（模糊查询）") @RequestParam(required = false) String position,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "created_at") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        UserQueryRequest query = new UserQueryRequest();
        query.setPage(page);
        query.setSize(size);
        query.setUsername(username);
        query.setEmail(email);
        query.setRealName(realName);
        query.setRole(role);
        query.setStatus(status);
        query.setPhone(phone);
        query.setDepartment(department);
        query.setPosition(position);
        query.setSortField(sortField);
        query.setSortDirection(sortDirection);
        
        PageResult<User> result = userService.getUserPage(query);
        return Result.success(result);
    }

    @Operation(summary = "查询所有用户", description = "查询所有用户列表")
    @GetMapping("/all")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @Operation(summary = "删除用户", description = "根据ID删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "批量删除用户", description = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> deleteUserBatch(@RequestBody List<Long> ids) {
        userService.deleteUserBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户状态：0-禁用，1-启用") @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @PutMapping("/{id}/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return Result.success();
    }

    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam(defaultValue = "123456") String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被使用")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@Parameter(description = "用户名") @RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }

    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被使用")
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@Parameter(description = "邮箱") @RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(exists);
    }

    @Operation(summary = "根据角色查询用户", description = "根据角色查询用户列表")
    @GetMapping("/role/{role}")
    public Result<List<User>> getUsersByRole(@Parameter(description = "角色") @PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return Result.success(users);
    }

    @Operation(summary = "获取用户统计信息", description = "获取用户相关的统计数据")
    @GetMapping("/statistics")
    public Result<Object> getUserStatistics() {
        Object statistics = userService.getUserStatistics();
        return Result.success(statistics);
    }
} 