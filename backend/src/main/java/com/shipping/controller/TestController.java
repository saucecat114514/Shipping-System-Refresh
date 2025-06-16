package com.shipping.controller;

import com.shipping.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@Tag(name = "系统测试", description = "系统测试相关接口")
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 系统健康检查
     */
    @Operation(summary = "系统健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "航运管理系统运行正常");
        data.put("version", "1.0.0");
        
        return Result.success("系统健康", data);
    }

    /**
     * 系统信息
     */
    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "航运管理系统");
        data.put("version", "1.0.0");
        data.put("description", "基于Spring Boot + Vue的航运管理系统");
        data.put("author", "学校项目");
        data.put("buildTime", LocalDateTime.now());
        
        return Result.success("系统信息", data);
    }
} 