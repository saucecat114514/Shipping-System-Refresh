package com.shipping.controller;

import com.shipping.service.AuditLogService;
import com.shipping.model.dto.AuditLogQueryRequest;
import com.shipping.model.entity.AuditLog;
import com.shipping.common.Result;
import com.shipping.common.PageResult;
import com.shipping.config.RoleInterceptor.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 审计日志控制器
 */
@RestController
@RequestMapping("/audit-logs")
@Tag(name = "审计日志管理", description = "审计日志相关接口")
@RequireRole({"ADMIN"})
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "分页查询审计日志", description = "分页查询审计日志列表")
    public Result<PageResult<AuditLog>> queryAuditLogs(@Valid AuditLogQueryRequest request) {
        PageResult<AuditLog> result = auditLogService.queryAuditLogs(request);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取审计日志详情", description = "根据ID获取审计日志详情")
    public Result<AuditLog> getAuditLogById(@PathVariable Long id) {
        AuditLog auditLog = auditLogService.getAuditLogById(id);
        if (auditLog == null) {
            return Result.error("审计日志不存在");
        }
        return Result.success(auditLog);
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "清理过期日志", description = "清理过期的审计日志")
    public Result<String> cleanExpiredLogs(@RequestParam(defaultValue = "90") int retentionDays) {
        auditLogService.cleanExpiredLogs(retentionDays);
        return Result.success("审计日志清理成功");
    }

    @GetMapping("/stats/user")
    @Operation(summary = "获取用户操作统计", description = "获取指定用户的操作统计")
    public Result<Object> getUserOperationStats(
            @RequestParam String username,
            @RequestParam(defaultValue = "30") int days) {
        Object stats = auditLogService.getUserOperationStats(username, days);
        return Result.success(stats);
    }

    @GetMapping("/stats/system")
    @Operation(summary = "获取系统操作统计", description = "获取系统整体操作统计")
    public Result<Object> getSystemOperationStats(@RequestParam(defaultValue = "30") int days) {
        Object stats = auditLogService.getSystemOperationStats(days);
        return Result.success(stats);
    }
} 