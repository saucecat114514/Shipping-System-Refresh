package com.shipping.service;

import com.shipping.model.entity.SystemConfig;
import com.shipping.model.dto.SystemConfigRequest;
import com.shipping.model.dto.SystemConfigQueryRequest;
import com.shipping.common.PageResult;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {

    /**
     * 添加系统配置
     */
    void addSystemConfig(SystemConfigRequest request);

    /**
     * 更新系统配置
     */
    void updateSystemConfig(Long id, SystemConfigRequest request);

    /**
     * 根据ID查询系统配置
     */
    SystemConfig getSystemConfigById(Long id);

    /**
     * 根据配置键查询系统配置
     */
    SystemConfig getSystemConfigByKey(String configKey);

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，不存在时返回默认值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 分页查询系统配置
     */
    PageResult<SystemConfig> getSystemConfigPage(SystemConfigQueryRequest query);

    /**
     * 查询所有系统配置
     */
    List<SystemConfig> getAllSystemConfigs();

    /**
     * 获取系统配置Map（键值对形式）
     */
    Map<String, String> getSystemConfigMap();

    /**
     * 删除系统配置
     */
    void deleteSystemConfig(Long id);

    /**
     * 根据配置键删除系统配置
     */
    void deleteSystemConfigByKey(String configKey);

    /**
     * 批量删除系统配置
     */
    void deleteSystemConfigBatch(List<Long> ids);

    /**
     * 根据配置键更新配置值
     */
    void updateConfigValue(String configKey, String configValue);

    /**
     * 批量更新系统配置
     */
    void updateSystemConfigBatch(List<SystemConfigRequest> requests);

    /**
     * 初始化默认系统配置
     */
    void initDefaultConfigs();
} 