-- 航运管理系统初始测试数据
-- 注意：为了方便测试，密码采用明文存储 "123456"
-- 生产环境应该使用BCrypt加密

-- 插入测试用户
INSERT INTO users (username, password, email, real_name, role, status, created_at, updated_at) VALUES
('admin', '123456', 'admin@shipping.com', '系统管理员', 'ADMIN', 1, NOW(), NOW()),
('dispatcher', '123456', 'dispatcher@shipping.com', '调度员', 'DISPATCHER', 1, NOW(), NOW()),
('customer1', '123456', 'customer1@shipping.com', '客户一', 'CUSTOMER', 1, NOW(), NOW()),
('customer2', '123456', 'customer2@shipping.com', '客户二', 'CUSTOMER', 1, NOW(), NOW());

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

-- 插入测试航线数据
INSERT INTO routes (route_number, name, origin_port_id, destination_port_id, distance, distance_nm, estimated_duration, status, description, created_at, updated_at) VALUES
('R001', '上海-新加坡航线', 1, 5, 5200.0, 2808.0, 144.0, 1, '连接上海港与新加坡港的主要航线', NOW(), NOW()),
('R002', '上海-洛杉矶航线', 1, 7, 11000.0, 5940.0, 288.0, 1, '跨太平洋航线，连接上海与洛杉矶', NOW(), NOW()),
('R003', '深圳-釜山航线', 3, 6, 1800.0, 972.0, 72.0, 1, '连接珠三角与韩国的重要航线', NOW(), NOW()),
('R004', '宁波-鹿特丹航线', 2, 8, 20000.0, 10800.0, 576.0, 1, '连接中国与欧洲的远洋航线', NOW(), NOW()),
('R005', '青岛-新加坡航线', 4, 5, 4800.0, 2592.0, 120.0, 1, '北方港口到东南亚的航线', NOW(), NOW());

-- 插入测试航次数据
INSERT INTO voyages (voyage_number, route_id, ship_id, departure_date, arrival_date, actual_departure_date, actual_arrival_date, status, created_at, updated_at) VALUES
('V20240101001', 1, 1, '2024-01-15 08:00:00', '2024-01-21 08:00:00', NULL, NULL, 'PLANNED', NOW(), NOW()),
('V20240101002', 2, 3, '2024-01-20 10:00:00', '2024-02-01 14:00:00', NULL, NULL, 'PLANNED', NOW(), NOW()),
('V20240101003', 3, 2, '2024-01-18 09:00:00', '2024-01-21 09:00:00', '2024-01-18 09:30:00', NULL, 'IN_PROGRESS', NOW(), NOW()),
('V20240101004', 4, 4, '2024-01-25 12:00:00', '2024-02-18 16:00:00', NULL, NULL, 'PLANNED', NOW(), NOW()),
('V20240101005', 5, 1, '2024-01-22 14:00:00', '2024-01-27 14:00:00', NULL, NULL, 'PLANNED', NOW(), NOW()),
('V20231201001', 1, 2, '2023-12-15 08:00:00', '2023-12-21 08:00:00', '2023-12-15 08:15:00', '2023-12-21 07:45:00', 'COMPLETED', NOW(), NOW());

-- 插入测试订单数据
INSERT INTO orders (order_number, customer_id, voyage_id, cargo_name, cargo_type, cargo_weight, cargo_volume, is_urgent, base_price, additional_fees, total_price, status, notes, created_at, updated_at) VALUES
('ORD20240101001', 3, 1, '电子产品', '普通货物', 25.50, 45.0, 0, 6500.00, 0.00, 6500.00, 'CONFIRMED', '精密仪器，请轻拿轻放', NOW(), NOW()),
('ORD20240101002', 4, 2, '机械设备', '普通货物', 120.00, 200.0, 0, 66000.00, 0.00, 66000.00, 'CONFIRMED', '重型机械，需要专业装卸', NOW(), NOW()),
('ORD20240101003', 3, 3, '化学原料', '危险品', 80.00, 100.0, 1, 14400.00, 5760.00, 20160.00, 'IN_TRANSIT', '危险品运输，需特殊处理', NOW(), NOW()),
('ORD20240101004', 4, NULL, '冷冻食品', '冷藏货物', 60.00, 80.0, 0, 0.00, 0.00, 0.00, 'PENDING', '需要冷链运输', NOW(), NOW()),
('ORD20240101005', 3, 5, '服装纺织品', '普通货物', 15.00, 120.0, 0, 3600.00, 0.00, 3600.00, 'CONFIRMED', '时装货物，避免受潮', NOW(), NOW()),
('ORD20231201001', 4, 6, '汽车零件', '普通货物', 45.00, 60.0, 0, 11700.00, 0.00, 11700.00, 'DELIVERED', '已完成交付', NOW(), NOW());

-- 插入一些AIS测试数据
INSERT INTO ais_data (mmsi, ship_id, longitude, latitude, speed, heading, timestamp, created_at) VALUES
('413123456', 1, 121.4648, 31.2304, 0.0, 0.0, '2024-01-15 08:00:00', NOW()),
('413234567', 2, 129.0756, 35.1796, 12.5, 180.0, '2024-01-18 12:00:00', NOW()),
('413345678', 3, 121.4648, 31.2304, 0.0, 0.0, '2024-01-20 10:00:00', NOW()),
('413456789', 4, 121.5750, 29.8776, 0.0, 0.0, '2024-01-25 12:00:00', NOW());

-- 插入系统配置数据
INSERT INTO system_config (config_key, config_value, description, created_at, updated_at) VALUES
('base_freight_rate', '0.5', '基础运价费率（元/吨/公里）', NOW(), NOW()),
('urgent_surcharge_rate', '0.2', '加急附加费率', NOW(), NOW()),
('dangerous_goods_rate', '0.3', '危险品附加费率', NOW(), NOW()),
('fuel_surcharge_rate', '0.1', '燃油附加费率', NOW(), NOW()),
('system_name', '航运管理系统', '系统名称', NOW(), NOW()),
('system_version', '1.0.0', '系统版本', NOW(), NOW()); 