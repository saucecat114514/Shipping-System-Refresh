-- 航运管理系统初始测试数据
-- 注意：密码都是经过BCrypt加密的 "123456"

-- 插入测试用户
INSERT INTO users (username, password, email, real_name, role, status, created_at, updated_at) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@shipping.com', '系统管理员', 'ADMIN', 1, NOW(), NOW()),
('dispatcher', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'dispatcher@shipping.com', '调度员', 'DISPATCHER', 1, NOW(), NOW()),
('customer1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'customer1@shipping.com', '客户一', 'CUSTOMER', 1, NOW(), NOW()),
('customer2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'customer2@shipping.com', '客户二', 'CUSTOMER', 1, NOW(), NOW());

-- 插入测试港口数据
INSERT INTO ports (name_cn, name_en, code, country, longitude, latitude, created_at, updated_at) VALUES
('上海港', 'Shanghai Port', 'CNSHA', '中国', 121.4648, 31.2304, NOW(), NOW()),
('宁波舟山港', 'Ningbo-Zhoushan Port', 'CNNGB', '中国', 121.5750, 29.8776, NOW(), NOW()),
('深圳港', 'Shenzhen Port', 'CNSZX', '中国', 114.0586, 22.5430, NOW(), NOW()),
('青岛港', 'Qingdao Port', 'CNTAO', '中国', 120.4658, 36.0986, NOW(), NOW()),
('新加坡港', 'Port of Singapore', 'SGSIN', '新加坡', 103.8198, 1.3521, NOW(), NOW()),
('釜山港', 'Port of Busan', 'KRPUS', '韩国', 129.0756, 35.1796, NOW(), NOW()),
('洛杉矶港', 'Port of Los Angeles', 'USLAX', '美国', -118.2437, 33.7701, NOW(), NOW()),
('鹿特丹港', 'Port of Rotterdam', 'NLRTM', '荷兰', 4.4777, 51.9225, NOW(), NOW());

-- 插入测试船舶数据
INSERT INTO ships (name, type_cn, type_en, flag, mmsi, imo_number, gross_tonnage, deadweight_tonnage, status, created_at, updated_at) VALUES
('海运明珠', '集装箱船', 'Container Ship', '中国', '413123456', 'IMO1234567', 75000.00, 85000.00, 0, NOW(), NOW()),
('远洋之星', '散货船', 'Bulk Carrier', '中国', '413234567', 'IMO2345678', 65000.00, 75000.00, 1, NOW(), NOW()),
('东方巨轮', '集装箱船', 'Container Ship', '中国', '413345678', 'IMO3456789', 80000.00, 90000.00, 0, NOW(), NOW()),
('蓝海先锋', '油轮', 'Oil Tanker', '中国', '413456789', 'IMO4567890', 55000.00, 65000.00, 2, NOW(), NOW());

-- 插入系统配置数据
INSERT INTO system_config (config_key, config_value, description, created_at, updated_at) VALUES
('base_freight_rate', '0.5', '基础运价费率（元/吨/公里）', NOW(), NOW()),
('urgent_surcharge_rate', '0.2', '加急附加费率', NOW(), NOW()),
('dangerous_goods_rate', '0.3', '危险品附加费率', NOW(), NOW()),
('fuel_surcharge_rate', '0.1', '燃油附加费率', NOW(), NOW()),
('system_name', '航运管理系统', '系统名称', NOW(), NOW()),
('system_version', '1.0.0', '系统版本', NOW(), NOW()); 