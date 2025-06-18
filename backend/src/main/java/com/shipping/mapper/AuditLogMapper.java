package com.shipping.mapper;

import com.shipping.model.entity.AuditLog;
import com.shipping.model.dto.AuditLogQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 审计日志Mapper接口
 */
@Mapper
public interface AuditLogMapper {

    /**
     * 新增审计日志
     */
    void insertAuditLog(AuditLog auditLog);

    /**
     * 根据ID查询审计日志
     */
    AuditLog selectAuditLogById(Long id);

    /**
     * 分页查询审计日志
     */
    List<AuditLog> selectAuditLogList(AuditLogQueryRequest request);

    /**
     * 查询审计日志总数
     */
    int countAuditLogs(AuditLogQueryRequest request);

    /**
     * 批量删除过期的审计日志
     */
    int deleteExpiredLogs(@Param("retentionDays") int retentionDays);

    /**
     * 获取用户操作统计
     */
    List<Map<String, Object>> getUserOperationStats(@Param("username") String username, @Param("days") int days);

    /**
     * 获取系统操作统计
     */
    List<Map<String, Object>> getSystemOperationStats(@Param("days") int days);

    /**
     * 获取模块操作统计
     */
    List<Map<String, Object>> getModuleOperationStats(@Param("days") int days);

    /**
     * 获取错误操作统计
     */
    List<Map<String, Object>> getErrorOperationStats(@Param("days") int days);
} 