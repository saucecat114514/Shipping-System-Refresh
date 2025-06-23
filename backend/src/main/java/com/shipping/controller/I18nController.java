package com.shipping.controller;

import com.shipping.common.Result;
import com.shipping.util.MessageUtil;
import com.shipping.config.RoleInterceptor.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 国际化演示控制器
 */
@RestController
@RequestMapping("/i18n")
@Tag(name = "国际化管理", description = "多语言支持相关接口")
public class I18nController {

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping("/message")
    @Operation(summary = "获取国际化消息", description = "根据key获取当前语言的消息")
    public Result<String> getMessage(@RequestParam String key) {
        String message = messageUtil.getMessage(key);
        return Result.success(message);
    }

    @GetMapping("/current-locale")
    @Operation(summary = "获取当前语言", description = "获取当前请求的语言设置")
    public Result<Map<String, Object>> getCurrentLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> result = new HashMap<>();
        result.put("language", locale.getLanguage());
        result.put("country", locale.getCountry());
        result.put("displayName", locale.getDisplayName());
        result.put("displayNameEnglish", locale.getDisplayName(Locale.ENGLISH));
        return Result.success(result);
    }

    @GetMapping("/demo/success")
    @Operation(summary = "演示成功消息", description = "演示不同语言的成功消息")
    public Result<Map<String, String>> demoSuccessMessage() {
        Map<String, String> messages = new HashMap<>();
        messages.put("current", messageUtil.getMessage("common.success"));
        messages.put("chinese", messageUtil.getChineseMessage("common.success"));
        messages.put("english", messageUtil.getEnglishMessage("common.success"));
        return Result.success(messages);
    }

    @GetMapping("/demo/user")
    @Operation(summary = "演示用户相关消息", description = "演示用户模块的国际化消息")
    public Result<Map<String, String>> demoUserMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("login.success", messageUtil.getMessage("user.login.success"));
        messages.put("create.success", messageUtil.getMessage("user.create.success"));
        messages.put("not.found", messageUtil.getMessage("user.not.found"));
        messages.put("password.wrong", messageUtil.getMessage("user.password.wrong"));
        return Result.success(messages);
    }

    @GetMapping("/demo/validation")
    @Operation(summary = "演示验证消息", description = "演示表单验证的国际化消息")
    public Result<Map<String, String>> demoValidationMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("not.null", messageUtil.getMessage("validation.not.null"));
        messages.put("not.blank", messageUtil.getMessage("validation.not.blank"));
        messages.put("email.invalid", messageUtil.getMessage("validation.email.invalid"));
        messages.put("phone.invalid", messageUtil.getMessage("validation.phone.invalid"));
        return Result.success(messages);
    }

    @GetMapping("/demo/permission-denied")
    @Operation(summary = "演示权限拒绝", description = "演示权限不足的消息（需要管理员权限）")
    @RequireRole({"ADMIN"})
    public Result<String> demoPermissionDenied() {
        return Result.success(messageUtil.getMessage("permission.denied"));
    }

    @GetMapping("/demo/role-required")
    @Operation(summary = "演示角色要求", description = "演示需要调度员权限的接口")
    @RequireRole({"DISPATCHER", "ADMIN"})
    public Result<String> demoRoleRequired() {
        return Result.success("您拥有调度员或管理员权限，可以访问此接口");
    }

    @PostMapping("/demo/audit-log")
    @Operation(summary = "演示审计日志", description = "演示会被记录到审计日志的操作")
    @RequireRole({"ADMIN", "DISPATCHER"})
    public Result<String> demoAuditLog(@RequestBody Map<String, Object> data) {
        // 这个操作会被审计日志拦截器记录
        return Result.success("操作已完成，已记录到审计日志");
    }
} 