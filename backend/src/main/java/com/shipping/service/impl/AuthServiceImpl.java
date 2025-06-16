package com.shipping.service.impl;

import com.shipping.exception.BusinessException;
import com.shipping.mapper.UserMapper;
import com.shipping.model.dto.LoginRequest;
import com.shipping.model.dto.LoginResponse;
import com.shipping.model.entity.User;
import com.shipping.service.AuthService;
import com.shipping.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查找用户
        User user = userMapper.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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