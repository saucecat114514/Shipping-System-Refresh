package com.shipping.service.impl;

import com.shipping.service.AuditLogService;
import com.shipping.mapper.AuditLogMapper;
import com.shipping.model.entity.AuditLog;
import com.shipping.model.dto.AuditLogQueryRequest;
import com.shipping.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务实现类
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    @Async
    public void saveAuditLogAsync(AuditLog auditLog) {
        try {
            saveAuditLog(auditLog);
        } catch (Exception e) {
            logger.error("异步保存审计日志失败", e);
        }
    }

    @Override
    @Transactional
    public void saveAuditLog(AuditLog auditLog) {
        try {
            auditLogMapper.insertAuditLog(auditLog);
        } catch (Exception e) {
            logger.error("保存审计日志失败", e);
            throw e;
        }
    }

    @Override
    public PageResult<AuditLog> queryAuditLogs(AuditLogQueryRequest request) {
        try {
            List<AuditLog> auditLogs = auditLogMapper.selectAuditLogList(request);
            int total = auditLogMapper.countAuditLogs(request);
            
            return new PageResult<>((long) total, auditLogs, request.getPageNum(), request.getPageSize());
        } catch (Exception e) {
            logger.error("查询审计日志失败", e);
            throw e;
        }
    }

    @Override
    public AuditLog getAuditLogById(Long id) {
        try {
            return auditLogMapper.selectAuditLogById(id);
        } catch (Exception e) {
            logger.error("根据ID查询审计日志失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void cleanExpiredLogs(int retentionDays) {
        try {
            int deletedCount = auditLogMapper.deleteExpiredLogs(retentionDays);
            logger.info("清理过期审计日志，删除{}条记录", deletedCount);
        } catch (Exception e) {
            logger.error("清理过期审计日志失败", e);
            throw e;
        }
    }

    @Override
    public Object getUserOperationStats(String username, int days) {
        try {
            List<Map<String, Object>> stats = auditLogMapper.getUserOperationStats(username, days);
            
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("days", days);
            result.put("operations", stats);
            
            // 计算总操作数
            int totalOperations = stats.stream()
                .mapToInt(stat -> ((Number) stat.get("count")).intValue())
                .sum();
            result.put("totalOperations", totalOperations);
            
            return result;
        } catch (Exception e) {
            logger.error("获取用户操作统计失败", e);
            throw e;
        }
    }

    @Override
    public Object getSystemOperationStats(int days) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("days", days);
            
            // 获取操作类型统计
            List<Map<String, Object>> operationStats = auditLogMapper.getSystemOperationStats(days);
            result.put("operationStats", operationStats);
            
            // 获取模块统计
            List<Map<String, Object>> moduleStats = auditLogMapper.getModuleOperationStats(days);
            result.put("moduleStats", moduleStats);
            
            // 获取错误统计
            List<Map<String, Object>> errorStats = auditLogMapper.getErrorOperationStats(days);
            result.put("errorStats", errorStats);
            
            // 计算总操作数和成功率
            int totalOperations = operationStats.stream()
                .mapToInt(stat -> ((Number) stat.get("count")).intValue())
                .sum();
            result.put("totalOperations", totalOperations);
            
            int errorCount = errorStats.stream()
                .mapToInt(stat -> ((Number) stat.get("count")).intValue())
                .sum();
            double successRate = totalOperations > 0 ? 
                (double)(totalOperations - errorCount) / totalOperations * 100 : 100.0;
            result.put("successRate", Math.round(successRate * 100) / 100.0);
            
            return result;
        } catch (Exception e) {
            logger.error("获取系统操作统计失败", e);
            throw e;
        }
    }
} 