package com.shipping.service;

import com.shipping.model.dto.LoginRequest;
import com.shipping.model.dto.LoginResponse;
import com.shipping.model.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     */
    LoginResponse register(RegisterRequest registerRequest);

    /**
     * 获取当前用户信息
     */
    LoginResponse getCurrentUser(String token);
} 