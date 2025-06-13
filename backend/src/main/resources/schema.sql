-- 航运管理系统数据库结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS shipping_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shipping_system;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role ENUM('ADMIN', 'DISPATCHER', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER' COMMENT '角色',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) COMMENT '用户表';

-- 港口表
CREATE TABLE ports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '港口ID',
    name_cn VARCHAR(100) NOT NULL COMMENT '港口名称（中文）',
    name_en VARCHAR(100) NOT NULL COMMENT '港口名称（英文）',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '港口代码（UN/LOCODE）',
    country VARCHAR(50) NOT NULL COMMENT '所属国家',
    longitude DECIMAL(10, 7) NOT NULL COMMENT '经度',
    latitude DECIMAL(10, 7) NOT NULL COMMENT '纬度',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (code),
    INDEX idx_country (country),
    INDEX idx_location (longitude, latitude)
) COMMENT '港口表';

-- 船舶表
CREATE TABLE ships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '船舶ID',
    name VARCHAR(100) NOT NULL COMMENT '船舶名称',
    type_cn VARCHAR(50) NOT NULL COMMENT '船舶类型（中文）',
    type_en VARCHAR(50) NOT NULL COMMENT '船舶类型（英文）',
    flag VARCHAR(50) NOT NULL COMMENT '船籍',
    mmsi VARCHAR(20) UNIQUE COMMENT 'MMSI（海事移动业务标识）',
    imo_number VARCHAR(20) UNIQUE COMMENT 'IMO编号',
    gross_tonnage DECIMAL(10, 2) COMMENT '总吨位（GRT）',
    deadweight_tonnage DECIMAL(10, 2) COMMENT '载重吨位（DWT）',
    current_speed DECIMAL(5, 2) COMMENT '当前航速（节）',
    current_heading DECIMAL(5, 2) COMMENT '当前航向（度）',
    current_longitude DECIMAL(10, 7) COMMENT '当前经度',
    current_latitude DECIMAL(10, 7) COMMENT '当前纬度',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '船舶状态：0-停泊，1-航行中，2-锚泊，3-维修',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_mmsi (mmsi),
    INDEX idx_imo (imo_number),
    INDEX idx_status (status),
    INDEX idx_current_location (current_longitude, current_latitude)
) COMMENT '船舶表';

-- 航线表
CREATE TABLE routes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '航线ID',
    route_number VARCHAR(20) NOT NULL UNIQUE COMMENT '航线编号',
    name VARCHAR(100) NOT NULL COMMENT '航线名称',
    origin_port_id BIGINT NOT NULL COMMENT '起始港口ID',
    destination_port_id BIGINT NOT NULL COMMENT '目的港口ID',
    distance DECIMAL(10, 2) NOT NULL COMMENT '航程距离（公里）',
    distance_nm DECIMAL(10, 2) NOT NULL COMMENT '航程距离（海里）',
    estimated_duration DECIMAL(8, 2) COMMENT '预计航行时间（小时）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '航线状态：0-停用，1-启用',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (origin_port_id) REFERENCES ports(id),
    FOREIGN KEY (destination_port_id) REFERENCES ports(id),
    INDEX idx_route_number (route_number),
    INDEX idx_origin_port (origin_port_id),
    INDEX idx_destination_port (destination_port_id),
    INDEX idx_status (status)
) COMMENT '航线表';

-- 航次表
CREATE TABLE voyages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '航次ID',
    voyage_number VARCHAR(20) NOT NULL UNIQUE COMMENT '航次编号',
    route_id BIGINT NOT NULL COMMENT '航线ID',
    ship_id BIGINT NOT NULL COMMENT '船舶ID',
    departure_date DATETIME NOT NULL COMMENT '计划出发日期',
    arrival_date DATETIME NOT NULL COMMENT '计划到达日期',
    actual_departure_date DATETIME COMMENT '实际出发日期',
    actual_arrival_date DATETIME COMMENT '实际到达日期',
    status ENUM('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PLANNED' COMMENT '航次状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (route_id) REFERENCES routes(id),
    FOREIGN KEY (ship_id) REFERENCES ships(id),
    INDEX idx_voyage_number (voyage_number),
    INDEX idx_route (route_id),
    INDEX idx_ship (ship_id),
    INDEX idx_departure_date (departure_date),
    INDEX idx_status (status)
) COMMENT '航次表';

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_number VARCHAR(20) NOT NULL UNIQUE COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    voyage_id BIGINT COMMENT '航次ID',
    cargo_name VARCHAR(100) NOT NULL COMMENT '货物名称',
    cargo_type VARCHAR(50) NOT NULL COMMENT '货物类型',
    cargo_weight DECIMAL(10, 2) NOT NULL COMMENT '货物重量（吨）',
    cargo_volume DECIMAL(10, 2) COMMENT '货物体积（立方米）',
    is_urgent TINYINT NOT NULL DEFAULT 0 COMMENT '是否加急：0-普通，1-加急',
    base_price DECIMAL(10, 2) NOT NULL COMMENT '基础运价',
    additional_fees DECIMAL(10, 2) DEFAULT 0 COMMENT '附加费用',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '总运费',
    status ENUM('PENDING', 'CONFIRMED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (voyage_id) REFERENCES voyages(id),
    INDEX idx_order_number (order_number),
    INDEX idx_customer (customer_id),
    INDEX idx_voyage (voyage_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT '订单表';

-- AIS数据表（船舶动态轨迹）
CREATE TABLE ais_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'AIS数据ID',
    mmsi VARCHAR(20) NOT NULL COMMENT 'MMSI',
    ship_id BIGINT COMMENT '船舶ID',
    longitude DECIMAL(10, 7) NOT NULL COMMENT '经度',
    latitude DECIMAL(10, 7) NOT NULL COMMENT '纬度',
    speed DECIMAL(5, 2) COMMENT '航速（节）',
    heading DECIMAL(5, 2) COMMENT '航向（度）',
    timestamp DATETIME NOT NULL COMMENT '时间戳',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (ship_id) REFERENCES ships(id),
    INDEX idx_mmsi (mmsi),
    INDEX idx_ship (ship_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_location (longitude, latitude),
    INDEX idx_mmsi_timestamp (mmsi, timestamp)
) COMMENT 'AIS数据表（船舶动态轨迹）';

-- 系统配置表
CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT NOT NULL COMMENT '配置值',
    description VARCHAR(255) COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key)
) COMMENT '系统配置表'; 