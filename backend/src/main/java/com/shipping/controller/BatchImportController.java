package com.shipping.controller;

import com.shipping.service.BatchImportService;
import com.shipping.model.dto.BatchImportRequest;
import com.shipping.model.dto.BatchImportResult;
import com.shipping.common.Result;
import com.shipping.config.RoleInterceptor.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 批量导入控制器
 */
@RestController
@RequestMapping("/api/batch-import")
@Tag(name = "批量导入管理", description = "数据批量导入相关接口")
@RequireRole({"ADMIN", "DISPATCHER"})
public class BatchImportController {

    @Autowired
    private BatchImportService batchImportService;

    @PostMapping("/upload")
    @Operation(summary = "批量导入数据", description = "上传文件进行批量数据导入")
    public Result<BatchImportResult> importData(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute BatchImportRequest request,
            HttpServletRequest httpRequest) {
        
        String username = (String) httpRequest.getAttribute("currentUser");
        if (username == null) {
            username = "system";
        }
        
        BatchImportResult result = batchImportService.importData(file, request, username);
        return Result.success(result);
    }

    @GetMapping("/result/{importId}")
    @Operation(summary = "获取导入结果", description = "根据导入ID获取导入结果")
    public Result<BatchImportResult> getImportResult(@PathVariable String importId) {
        BatchImportResult result = batchImportService.getImportResult(importId);
        if (result == null) {
            return Result.error("导入记录不存在");
        }
        return Result.success(result);
    }

    @GetMapping("/history")
    @Operation(summary = "获取导入历史", description = "获取用户的导入历史记录")
    public Result<List<BatchImportResult>> getImportHistory(
            @RequestParam(defaultValue = "30") int days,
            HttpServletRequest request) {
        
        String username = (String) request.getAttribute("currentUser");
        if (username == null) {
            username = "system";
        }
        
        List<BatchImportResult> history = batchImportService.getImportHistory(username, days);
        return Result.success(history);
    }

    @GetMapping("/template/{importType}")
    @Operation(summary = "下载导入模板", description = "下载指定类型的导入模板文件")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String importType) {
        try {
            byte[] template = batchImportService.getImportTemplate(importType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", importType + "_template.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/types")
    @Operation(summary = "获取支持的导入类型", description = "获取系统支持的所有导入类型")
    public Result<List<String>> getSupportedImportTypes() {
        List<String> types = batchImportService.getSupportedImportTypes();
        return Result.success(types);
    }

    @GetMapping("/export-errors/{importId}")
    @Operation(summary = "导出错误数据", description = "导出导入过程中的错误数据")
    public ResponseEntity<byte[]> exportErrorData(@PathVariable String importId) {
        try {
            byte[] errorData = batchImportService.exportErrorData(importId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "import_errors_" + importId + ".xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(errorData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 