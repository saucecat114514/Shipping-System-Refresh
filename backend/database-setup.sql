-- MySQL 数据库初始化脚本
-- 在启动Spring Boot应用之前，请先在MySQL中执行此脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shipping_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选，如果需要专用数据库用户）
-- CREATE USER 'shipping_user'@'localhost' IDENTIFIED BY 'shipping_password';
-- GRANT ALL PRIVILEGES ON shipping_system.* TO 'shipping_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 使用数据库
USE shipping_system;

-- 验证数据库创建成功
SELECT 'Database shipping_system created successfully!' as message; 