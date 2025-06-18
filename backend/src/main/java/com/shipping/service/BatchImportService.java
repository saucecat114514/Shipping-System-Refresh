package com.shipping.service;

import com.shipping.model.dto.BatchImportRequest;
import com.shipping.model.dto.BatchImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 批量导入服务接口
 */
public interface BatchImportService {

    /**
     * 批量导入数据
     */
    BatchImportResult importData(MultipartFile file, BatchImportRequest request, String username);

    /**
     * 获取导入结果
     */
    BatchImportResult getImportResult(String importId);

    /**
     * 获取导入历史
     */
    List<BatchImportResult> getImportHistory(String username, int days);

    /**
     * 解析Excel文件
     */
    List<Map<String, Object>> parseExcelFile(MultipartFile file, String importType);

    /**
     * 验证数据格式
     */
    Map<String, Object> validateData(List<Map<String, Object>> data, String importType);

    /**
     * 获取导入模板
     */
    byte[] getImportTemplate(String importType);

    /**
     * 获取支持的导入类型
     */
    List<String> getSupportedImportTypes();

    /**
     * 导出错误数据
     */
    byte[] exportErrorData(String importId);
} 