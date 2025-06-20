package com.shipping.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具
 * 用于生成BCrypt加密的密码哈希值
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成测试用户的密码哈希
        String adminPassword = "admin123";
        String dispatcherPassword = "dispatcher123";  
        String customerPassword = "customer123";
        
        System.out.println("admin123 -> " + encoder.encode(adminPassword));
        System.out.println("dispatcher123 -> " + encoder.encode(dispatcherPassword));
        System.out.println("customer123 -> " + encoder.encode(customerPassword));
        
        // 验证哈希是否正确
        String testHash = encoder.encode(adminPassword);
        System.out.println("验证 admin123: " + encoder.matches(adminPassword, testHash));
    }
} 