package com.shipping.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 消息工具类
 */
@Component
public class MessageUtil {

    @Autowired
    private MessageSource messageSource;

    /**
     * 获取消息
     */
    public String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

    /**
     * 获取指定语言的消息
     */
    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, key, locale);
    }

    /**
     * 获取中文消息
     */
    public String getChineseMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 获取英文消息
     */
    public String getEnglishMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, Locale.ENGLISH);
    }

    /**
     * 检查消息是否存在
     */
    public boolean hasMessage(String key) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage(key, null, locale);
            return !key.equals(message);
        } catch (Exception e) {
            return false;
        }
    }
} 