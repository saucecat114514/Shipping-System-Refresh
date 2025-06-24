-- 检查并添加订单表的港口字段
-- 如果字段不存在则添加

-- 添加origin_port_id字段
ALTER TABLE orders ADD COLUMN IF NOT EXISTS origin_port_id BIGINT COMMENT '起始港口ID' AFTER voyage_id;

-- 添加destination_port_id字段  
ALTER TABLE orders ADD COLUMN IF NOT EXISTS destination_port_id BIGINT COMMENT '目的港口ID' AFTER origin_port_id;

-- 修改status枚举，添加PENDING_ASSIGNMENT状态
ALTER TABLE orders MODIFY COLUMN status ENUM('PENDING_ASSIGNMENT', 'PENDING', 'CONFIRMED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态';

-- 添加外键约束（如果不存在）
ALTER TABLE orders ADD CONSTRAINT IF NOT EXISTS fk_orders_origin_port FOREIGN KEY (origin_port_id) REFERENCES ports(id);
ALTER TABLE orders ADD CONSTRAINT IF NOT EXISTS fk_orders_destination_port FOREIGN KEY (destination_port_id) REFERENCES ports(id);

-- 添加索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_origin_port ON orders(origin_port_id);
CREATE INDEX IF NOT EXISTS idx_destination_port ON orders(destination_port_id); 