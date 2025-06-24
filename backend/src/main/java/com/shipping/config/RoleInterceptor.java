package com.shipping.config;

import com.shipping.util.JwtUtil;
import com.shipping.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipping.common.Result;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 角色权限拦截器
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 角色权限注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequireRole {
        String[] value() default {}; // 允许的角色列表
        boolean requireLogin() default true; // 是否需要登录
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 跨域预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 检查是否为HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        // 检查方法上的注解
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        
        RequireRole requireRole = methodAnnotation != null ? methodAnnotation : classAnnotation;
        
        // 如果没有权限注解，直接放行（不需要任何权限检查）
        if (requireRole == null) {
            return true;
        }

        // 检查是否需要登录
        if (requireRole.requireLogin()) {
            String token = getTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return handleUnauthorized(response, "用户未登录或token无效");
            }

            // 获取用户角色
            String userRole = jwtUtil.getRoleFromToken(token);
            if (userRole == null) {
                return handleUnauthorized(response, "无法获取用户角色信息");
            }

                    // 检查角色权限
        String[] allowedRoles = requireRole.value();
        if (allowedRoles.length > 0) {
            List<String> roleList = Arrays.asList(allowedRoles);
            if (!roleList.contains(userRole)) {
                return handleForbidden(response, "权限不足，需要角色：" + String.join(",", allowedRoles));
            }
        }

            // 将用户信息添加到请求属性中
            String username = jwtUtil.getUsernameFromToken(token);
            request.setAttribute("currentUser", username);
            request.setAttribute("currentUserRole", userRole);
        }

        return true;
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 处理未认证的请求
     */
    private boolean handleUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = new Result<>();
        result.setCode(401);
        result.setMsg(message);
        result.setData(null);
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }

    /**
     * 处理权限不足的请求
     */
    private boolean handleForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Object> result = new Result<>();
        result.setCode(403);
        result.setMsg(message);
        result.setData(null);
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
} 