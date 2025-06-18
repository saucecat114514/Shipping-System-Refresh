package com.shipping.service;

import com.shipping.model.entity.AuditLog;
import com.shipping.model.dto.AuditLogQueryRequest;
import com.shipping.common.PageResult;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {

    /**
     * 异步保存审计日志
     */
    void saveAuditLogAsync(AuditLog auditLog);

    /**
     * 保存审计日志
     */
    void saveAuditLog(AuditLog auditLog);

    /**
     * 分页查询审计日志
     */
    PageResult<AuditLog> queryAuditLogs(AuditLogQueryRequest request);

    /**
     * 根据ID获取审计日志
     */
    AuditLog getAuditLogById(Long id);

    /**
     * 清理过期的审计日志
     */
    void cleanExpiredLogs(int retentionDays);

    /**
     * 获取用户操作统计
     */
    Object getUserOperationStats(String username, int days);

    /**
     * 获取系统操作统计
     */
    Object getSystemOperationStats(int days);
} 