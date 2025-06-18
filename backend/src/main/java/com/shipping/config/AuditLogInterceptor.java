package com.shipping.config;

import com.shipping.model.entity.AuditLog;
import com.shipping.service.AuditLogService;
import com.shipping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志拦截器
 */
@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        
        try {
            // 只记录API接口的日志
            String requestURI = request.getRequestURI();
            if (!requestURI.startsWith("/api/") || requestURI.contains("/test/")) {
                return;
            }

            // 计算响应时间
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            long responseTime = startTime != null ? System.currentTimeMillis() - startTime : 0;

            // 创建审计日志
            AuditLog auditLog = new AuditLog();
            
            // 设置用户信息
            String token = getTokenFromRequest(request);
            if (token != null && jwtUtil.validateToken(token)) {
                auditLog.setUsername(jwtUtil.getUsernameFromToken(token));
                auditLog.setUserRole(jwtUtil.getRoleFromToken(token));
            } else {
                auditLog.setUsername("anonymous");
                auditLog.setUserRole("GUEST");
            }

            // 设置操作信息
            auditLog.setOperation(getOperationType(request.getMethod()));
            auditLog.setModule(getModuleFromURI(requestURI));
            auditLog.setMethod(getMethodInfo(handler));
            auditLog.setRequestUrl(requestURI);
            auditLog.setRequestMethod(request.getMethod());
            
            // 设置请求参数
            auditLog.setRequestParams(getRequestParams(request));
            
            // 设置客户端信息
            auditLog.setClientIp(getClientIp(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
            
            // 设置响应信息
            auditLog.setResult(ex == null ? "SUCCESS" : "ERROR");
            if (ex != null) {
                auditLog.setErrorMsg(ex.getMessage());
            }
            auditLog.setResponseTime(responseTime);
            auditLog.setOperationTime(LocalDateTime.now());
            auditLog.setCreatedAt(LocalDateTime.now());

            // 异步保存审计日志
            auditLogService.saveAuditLogAsync(auditLog);
            
        } catch (Exception e) {
            // 审计日志记录失败不应影响正常业务
            System.err.println("审计日志记录失败: " + e.getMessage());
        }
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
     * 根据HTTP方法获取操作类型
     */
    private String getOperationType(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return "QUERY";
            case "POST":
                return "CREATE";
            case "PUT":
            case "PATCH":
                return "UPDATE";
            case "DELETE":
                return "DELETE";
            default:
                return "OTHER";
        }
    }

    /**
     * 从URI中提取模块名
     */
    private String getModuleFromURI(String uri) {
        String[] parts = uri.split("/");
        if (parts.length >= 3) {
            return parts[2].toUpperCase(); // /api/users -> USERS
        }
        return "UNKNOWN";
    }

    /**
     * 获取方法信息
     */
    private String getMethodInfo(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.getMethod().getName();
        }
        return "unknown";
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();
            
            // 获取URL参数
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    params.put(paramName, paramValues[0]);
                } else {
                    params.put(paramName, paramValues);
                }
            }
            
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多级代理的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
} 