package com.shipping.service;

import com.shipping.model.dto.LoginRequest;
import com.shipping.model.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 获取当前用户信息
     */
    LoginResponse getCurrentUser(String token);
} 