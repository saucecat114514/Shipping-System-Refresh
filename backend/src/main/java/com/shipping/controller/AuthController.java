package com.shipping.controller;

import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import com.shipping.exception.BusinessException;
import com.shipping.model.dto.LoginRequest;
import com.shipping.model.dto.LoginResponse;
import com.shipping.model.dto.RegisterRequest;
import com.shipping.model.entity.User;
import com.shipping.service.AuthService;
import com.shipping.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、注册等认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "用户通过用户名和密码登录系统")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("用户登录请求: {}", loginRequest.getUsername());
        
        LoginResponse loginResponse = authService.login(loginRequest);
        
        logger.info("用户 {} 登录成功", loginRequest.getUsername());
        return Result.success("登录成功", loginResponse);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "新用户注册，默认角色为客户")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("用户注册请求: {}", registerRequest.getUsername());
        
        LoginResponse registerResponse = authService.register(registerRequest);
        
        logger.info("用户 {} 注册成功", registerRequest.getUsername());
        return Result.success("注册成功", registerResponse);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "用户登出系统")
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("Authorization") String token) {
        logger.info("用户登出请求");
        
        // 简化实现：由于JWT是无状态的，客户端删除token即可
        // 在更复杂的系统中，可以维护一个黑名单
        
        logger.info("用户登出成功");
        return Result.success("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/current-user")
    @RequireRole(value = {}, requireLogin = true)
    public Result<User> getCurrentUser(HttpServletRequest request) {
        String currentUsername = (String) request.getAttribute("currentUser");
        if (currentUsername == null) {
            throw new BusinessException("未获取到当前用户信息");
        }
        
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        return Result.success(user);
    }
} 