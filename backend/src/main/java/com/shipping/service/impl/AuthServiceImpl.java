package com.shipping.service.impl;

import com.shipping.exception.BusinessException;
import com.shipping.mapper.UserMapper;
import com.shipping.model.dto.LoginRequest;
import com.shipping.model.dto.LoginResponse;
import com.shipping.model.dto.RegisterRequest;
import com.shipping.model.entity.User;
import com.shipping.service.AuthService;
import com.shipping.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查找用户
        User user = userMapper.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码（使用明文比较）
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());

        logger.info("用户 {} 登录成功，角色: {}", user.getUsername(), user.getRole());
        return response;
    }

    @Override
    public LoginResponse register(RegisterRequest registerRequest) {
        logger.info("用户注册请求: {}", registerRequest.getUsername());

        // 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException("密码与确认密码不一致");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(registerRequest.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingEmailUser = userMapper.findByEmail(registerRequest.getEmail());
        if (existingEmailUser != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword()); // 注意：实际项目中应该加密密码
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRealName(registerRequest.getRealName());
        newUser.setRole("CUSTOMER"); // 默认角色为客户
        newUser.setStatus(1); // 默认启用状态
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // 保存用户到数据库
        int result = userMapper.insert(newUser);
        if (result <= 0) {
            throw new BusinessException("用户注册失败");
        }

        // 检查是否成功获取生成的ID
        if (newUser.getId() == null) {
            throw new BusinessException("用户注册失败，无法获取用户ID");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(newUser.getId(), newUser.getUsername(), newUser.getRole());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(newUser.getId());
        response.setUsername(newUser.getUsername());
        response.setRealName(newUser.getRealName());
        response.setRole(newUser.getRole());
        response.setEmail(newUser.getEmail());

        logger.info("用户 {} 注册成功，角色: {}", newUser.getUsername(), newUser.getRole());
        return response;
    }

    @Override
    public LoginResponse getCurrentUser(String token) {
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证令牌
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "令牌无效或已过期");
        }

        // 从令牌获取用户信息
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            throw new BusinessException(401, "无法获取用户信息");
        }

        // 查询用户详细信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());

        return response;
    }
} 