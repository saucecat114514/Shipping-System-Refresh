package com.shipping.service.impl;

import com.shipping.service.SystemConfigService;
import com.shipping.mapper.SystemConfigMapper;
import com.shipping.model.entity.SystemConfig;
import com.shipping.model.dto.SystemConfigRequest;
import com.shipping.model.dto.SystemConfigQueryRequest;
import com.shipping.common.PageResult;
import com.shipping.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    @Transactional
    public void addSystemConfig(SystemConfigRequest request) {
        logger.info("添加系统配置：{}", request.getConfigKey());
        
        // 检查配置键是否已存在
        SystemConfig existing = systemConfigMapper.selectByKey(request.getConfigKey());
        if (existing != null) {
            throw new BusinessException("配置键已存在");
        }

        SystemConfig systemConfig = convertToEntity(request);
        systemConfig.setCreatedAt(LocalDateTime.now());
        systemConfig.setUpdatedAt(LocalDateTime.now());
        
        systemConfigMapper.insert(systemConfig);
        logger.info("系统配置添加成功，ID：{}", systemConfig.getId());
    }

    @Override
    @Transactional
    public void updateSystemConfig(Long id, SystemConfigRequest request) {
        logger.info("更新系统配置，ID：{}", id);
        
        SystemConfig existing = systemConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("系统配置不存在");
        }

        // 检查新的配置键是否与其他记录冲突
        if (!existing.getConfigKey().equals(request.getConfigKey())) {
            SystemConfig conflicting = systemConfigMapper.selectByKey(request.getConfigKey());
            if (conflicting != null && !conflicting.getId().equals(id)) {
                throw new BusinessException("配置键已被其他配置使用");
            }
        }

        SystemConfig systemConfig = convertToEntity(request);
        systemConfig.setId(id);
        systemConfig.setCreatedAt(existing.getCreatedAt());
        systemConfig.setUpdatedAt(LocalDateTime.now());
        
        systemConfigMapper.update(systemConfig);
        logger.info("系统配置更新成功");
    }

    @Override
    public SystemConfig getSystemConfigById(Long id) {
        logger.info("查询系统配置，ID：{}", id);
        SystemConfig systemConfig = systemConfigMapper.selectById(id);
        if (systemConfig == null) {
            throw new BusinessException("系统配置不存在");
        }
        return systemConfig;
    }

    @Override
    public SystemConfig getSystemConfigByKey(String configKey) {
        logger.info("根据配置键查询系统配置：{}", configKey);
        return systemConfigMapper.selectByKey(configKey);
    }

    @Override
    public String getConfigValue(String configKey) {
        SystemConfig config = systemConfigMapper.selectByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    public PageResult<SystemConfig> getSystemConfigPage(SystemConfigQueryRequest query) {
        logger.info("分页查询系统配置：页码={}，每页大小={}", query.getPage(), query.getSize());
        
        int offset = (query.getPage() - 1) * query.getSize();
        List<SystemConfig> configList = systemConfigMapper.selectPage(query, offset, query.getSize());
        int total = systemConfigMapper.selectCount(query);
        
        return new PageResult<SystemConfig>((long) total, configList, query.getPage(), query.getSize());
    }

    @Override
    public List<SystemConfig> getAllSystemConfigs() {
        logger.info("查询所有系统配置");
        return systemConfigMapper.selectAll();
    }

    @Override
    public Map<String, String> getSystemConfigMap() {
        logger.info("获取系统配置Map");
        List<SystemConfig> configs = systemConfigMapper.selectAll();
        Map<String, String> configMap = new HashMap<>();
        for (SystemConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    @Override
    @Transactional
    public void deleteSystemConfig(Long id) {
        logger.info("删除系统配置，ID：{}", id);
        
        SystemConfig existing = systemConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("系统配置不存在");
        }
        
        systemConfigMapper.deleteById(id);
        logger.info("系统配置删除成功");
    }

    @Override
    @Transactional
    public void deleteSystemConfigByKey(String configKey) {
        logger.info("根据配置键删除系统配置：{}", configKey);
        
        SystemConfig existing = systemConfigMapper.selectByKey(configKey);
        if (existing == null) {
            throw new BusinessException("系统配置不存在");
        }
        
        systemConfigMapper.deleteByKey(configKey);
        logger.info("系统配置删除成功");
    }

    @Override
    @Transactional
    public void deleteSystemConfigBatch(List<Long> ids) {
        logger.info("批量删除系统配置，数量：{}", ids.size());
        systemConfigMapper.deleteBatch(ids);
        logger.info("批量删除系统配置成功");
    }

    @Override
    @Transactional
    public void updateConfigValue(String configKey, String configValue) {
        logger.info("更新配置值：{} = {}", configKey, configValue);
        
        SystemConfig existing = systemConfigMapper.selectByKey(configKey);
        if (existing == null) {
            throw new BusinessException("系统配置不存在");
        }
        
        systemConfigMapper.updateValueByKey(configKey, configValue);
        logger.info("配置值更新成功");
    }

    @Override
    @Transactional
    public void updateSystemConfigBatch(List<SystemConfigRequest> requests) {
        logger.info("批量更新系统配置，数量：{}", requests.size());
        
        List<SystemConfig> configList = requests.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        LocalDateTime now = LocalDateTime.now();
        configList.forEach(config -> config.setUpdatedAt(now));
        
        systemConfigMapper.updateBatch(configList);
        logger.info("批量更新系统配置成功");
    }

    @Override
    @Transactional
    public void initDefaultConfigs() {
        logger.info("初始化默认系统配置");
        
        // 检查是否已经初始化过
        if (systemConfigMapper.selectByKey("system.initialized") != null) {
            logger.info("系统配置已初始化，跳过");
            return;
        }
        
        // 初始化默认配置
        addDefaultConfig("system.name", "航运管理系统", "系统名称");
        addDefaultConfig("system.version", "1.0.0", "系统版本");
        addDefaultConfig("system.company", "华东航运有限公司", "公司名称");
        addDefaultConfig("freight.base_rate", "0.5", "基础运费率（元/吨/公里）");
        addDefaultConfig("freight.urgent_surcharge", "20", "紧急货物附加费率（%）");
        addDefaultConfig("freight.dangerous_surcharge", "30", "危险品附加费率（%）");
        addDefaultConfig("freight.refrigerated_surcharge", "50", "冷藏货物附加费（元/吨）");
        addDefaultConfig("freight.liquid_surcharge", "30", "液体货物附加费率（%）");
        addDefaultConfig("ais.data_retention_days", "30", "AIS数据保留天数");
        addDefaultConfig("ais.update_interval", "60", "AIS数据更新间隔（秒）");
        
        // 标记已初始化
        addDefaultConfig("system.initialized", "true", "系统初始化标记");
        
        logger.info("默认系统配置初始化完成");
    }

    /**
     * 添加默认配置
     */
    private void addDefaultConfig(String key, String value, String description) {
        if (systemConfigMapper.selectByKey(key) == null) {
            SystemConfig config = new SystemConfig(key, value, description);
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
            systemConfigMapper.insert(config);
        }
    }

    /**
     * 转换DTO为实体
     */
    private SystemConfig convertToEntity(SystemConfigRequest request) {
        SystemConfig systemConfig = new SystemConfig();
        BeanUtils.copyProperties(request, systemConfig);
        return systemConfig;
    }
} 