package com.shipping.config;

import com.shipping.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用启动配置
 * 在应用启动时执行初始化操作
 */
@Component
public class ApplicationStartupConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupConfig.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("========== 应用启动初始化开始 ==========");
        
        try {
            // 初始化系统配置
            initSystemConfig();
            
            logger.info("========== 应用启动初始化完成 ==========");
        } catch (Exception e) {
            logger.error("应用启动初始化失败", e);
            throw e;
        }
    }

    /**
     * 初始化系统配置
     */
    private void initSystemConfig() {
        try {
            logger.info("开始初始化系统配置...");
            systemConfigService.initDefaultConfigs();
            logger.info("系统配置初始化完成");
        } catch (Exception e) {
            logger.error("系统配置初始化失败", e);
            // 这里不抛出异常，避免影响应用启动
        }
    }
} 