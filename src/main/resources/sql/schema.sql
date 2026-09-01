-- ============================================================
-- 在线电脑商城系统 - 数据库脚本
-- 数据库: computer_shop
-- 6张表: admin, user, supplier, product, orders, order_item
-- 亮点: 触发器、视图、索引、乐观锁、中间表解决多对多
-- ============================================================

CREATE DATABASE IF NOT EXISTS computer_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE computer_shop;

-- ============================================================
-- 一、建表
-- ============================================================

-- 1. 管理员表
DROP TABLE IF EXISTS admin;
CREATE TABLE admin (
    admin_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    admin_name  VARCHAR(64)  NOT NULL UNIQUE COMMENT '管理员用户名',
    password    VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name   VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 2. 用户表(买家)
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    user_id       INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username      VARCHAR(64)  NOT NULL UNIQUE COMMENT '用户名',
    password      VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name     VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    address       VARCHAR(256) DEFAULT NULL COMMENT '收货地址',
    register_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    status        TINYINT      DEFAULT 1 COMMENT '状态: 0-禁用 1-正常'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 供应商表
DROP TABLE IF EXISTS supplier;
CREATE TABLE supplier (
    supplier_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '供应商ID',
    supplier_name VARCHAR(128) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(64) DEFAULT NULL COMMENT '联系人',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    address       VARCHAR(256) DEFAULT NULL COMMENT '地址',
    description   VARCHAR(512) DEFAULT NULL COMMENT '描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 4. 商品表 (含乐观锁version字段)
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    product_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    category     VARCHAR(32)  DEFAULT NULL COMMENT '分类: 笔记本/台式机/配件',
    brand        VARCHAR(64)  DEFAULT NULL COMMENT '品牌',
    model        VARCHAR(64)  DEFAULT NULL COMMENT '型号',
    price        DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    stock        INT          NOT NULL DEFAULT 0 COMMENT '库存',
    description  TEXT         DEFAULT NULL COMMENT '商品描述',
    image_url    VARCHAR(256) DEFAULT NULL COMMENT '图片URL',
    supplier_id  INT          DEFAULT NULL COMMENT '供应商ID(外键)',
    status       TINYINT      DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    version      INT          DEFAULT 0 COMMENT '乐观锁版本号，防止超卖',
    KEY idx_supplier (supplier_id),
    KEY idx_category (category),
    KEY idx_brand (brand)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 5. 订单表
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    order_id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no        VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单编号',
    user_id         INT          NOT NULL COMMENT '用户ID(外键)',
    total_amount    DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    total_count     INT          NOT NULL DEFAULT 0 COMMENT '商品总数',
    status          VARCHAR(32)  DEFAULT '待付款' COMMENT '状态: 待付款/已付款/已发货/已完成/已取消',
    receiver_name   VARCHAR(64)  DEFAULT NULL COMMENT '收货人姓名',
    receiver_phone  VARCHAR(20)  DEFAULT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(256) DEFAULT NULL COMMENT '收货地址',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time        DATETIME     DEFAULT NULL COMMENT '支付时间',
    deliver_time    DATETIME     DEFAULT NULL COMMENT '发货时间',
    finish_time     DATETIME     DEFAULT NULL COMMENT '完成时间',
    KEY idx_user (user_id),
    KEY idx_status (status),
    KEY idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 6. 订单项表(中间表) - 解决订单与商品的多对多关系
--    一个订单包含多个商品，一个商品可出现在多个订单中
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    item_id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id     INT          NOT NULL COMMENT '订单ID(外键)',
    product_id   INT          NOT NULL COMMENT '商品ID(外键)',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称(下单时快照)',
    price        DECIMAL(10,2) NOT NULL COMMENT '商品单价(下单时快照)',
    count        INT          NOT NULL DEFAULT 1 COMMENT '购买数量',
    subtotal     DECIMAL(10,2) NOT NULL COMMENT '小计 = price * count',
    KEY idx_order (order_id),
    KEY idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表(中间表)';

-- ============================================================
-- 二、索引优化
-- ============================================================
-- 为高频查询字段创建索引，提升查询性能
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_product_name ON product(product_name);
CREATE INDEX idx_orders_create_time ON orders(create_time);
CREATE INDEX idx_order_item_order_product ON order_item(order_id, product_id);

-- ============================================================
-- 三、视图
-- ============================================================

-- 视图1: 订单详情视图 (关联订单、用户、订单项)
DROP VIEW IF EXISTS v_order_detail;
CREATE VIEW v_order_detail AS
SELECT
    o.order_id,
    o.order_no,
    u.username,
    u.real_name,
    o.total_amount,
    o.total_count,
    o.status,
    o.receiver_name,
    o.receiver_phone,
    o.receiver_address,
    o.create_time,
    o.pay_time
FROM orders o
JOIN user u ON o.user_id = u.user_id;

-- 视图2: 商品销售统计视图 (按商品统计销量和销售额)
DROP VIEW IF EXISTS v_product_sales;
CREATE VIEW v_product_sales AS
SELECT
    p.product_id,
    p.product_name,
    p.category,
    p.brand,
    p.price,
    p.stock,
    COALESCE(SUM(oi.count), 0) AS total_sold,
    COALESCE(SUM(oi.subtotal), 0) AS total_revenue
FROM product p
LEFT JOIN order_item oi ON p.product_id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.order_id AND o.status != '已取消'
GROUP BY p.product_id, p.product_name, p.category, p.brand, p.price, p.stock;

-- 视图3: 用户消费统计视图
DROP VIEW IF EXISTS v_user_consumption;
CREATE VIEW v_user_consumption AS
SELECT
    u.user_id,
    u.username,
    u.real_name,
    u.phone,
    COUNT(o.order_id) AS order_count,
    COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id AND o.status != '已取消'
GROUP BY u.user_id, u.username, u.real_name, u.phone;

-- ============================================================
-- 四、触发器
-- ============================================================

-- 触发器1: 下单后自动扣减库存 (创建订单项时触发)
DELIMITER //
DROP TRIGGER IF EXISTS trg_after_order_item_insert//
CREATE TRIGGER trg_after_order_item_insert
AFTER INSERT ON order_item
FOR EACH ROW
BEGIN
    -- 扣减对应商品库存
    UPDATE product SET stock = stock - NEW.count
    WHERE product_id = NEW.product_id;
END//
DELIMITER ;

-- 触发器2: 订单取消时自动恢复库存
DELIMITER //
DROP TRIGGER IF EXISTS trg_after_order_cancel//
CREATE TRIGGER trg_after_order_cancel
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    -- 当订单状态变为"已取消"时，恢复库存
    IF NEW.status = '已取消' AND OLD.status != '已取消' THEN
        UPDATE product p
        JOIN order_item oi ON p.product_id = oi.product_id
        SET p.stock = p.stock + oi.count
        WHERE oi.order_id = NEW.order_id;
    END IF;
END//
DELIMITER ;

-- 触发器3: 插入商品时自动设置version初始值
DELIMITER //
DROP TRIGGER IF EXISTS trg_before_product_insert//
CREATE TRIGGER trg_before_product_insert
BEFORE INSERT ON product
FOR EACH ROW
BEGIN
    IF NEW.version IS NULL OR NEW.version < 0 THEN
        SET NEW.version = 0;
    END IF;
END//
DELIMITER ;

-- ============================================================
-- 五、初始数据
-- ============================================================

-- 管理员 (密码123456的BCrypt加密值)
INSERT INTO admin (admin_name, password, real_name, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu', '系统管理员', '13800138000');

-- 用户 (密码123456的BCrypt加密值)
INSERT INTO user (username, password, real_name, phone, email, address) VALUES
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu', '张三', '13900139001', 'zhangsan@example.com', '北京市朝阳区'),
('user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu', '李四', '13900139002', 'lisi@example.com', '上海市浦东新区');

-- 供应商
INSERT INTO supplier (supplier_name, contact_person, phone, address, description) VALUES
('联想集团供应商', '王经理', '010-12345678', '北京市海淀区', '笔记本电脑、台式机供应商'),
('戴尔科技供应商', '李经理', '021-87654321', '上海市徐汇区', '服务器、工作站供应商'),
('罗技科技供应商', '张经理', '0755-11112222', '深圳市南山区', '键鼠、外设供应商');

-- 商品
INSERT INTO product (product_name, category, brand, model, price, stock, description, supplier_id, version) VALUES
('联想小新Pro16', '笔记本', '联想', 'Pro16-2024', 5999.00, 100, '16英寸轻薄本，锐龙R7处理器，2.5K屏幕', 1, 0),
('戴尔XPS13', '笔记本', '戴尔', 'XPS13-9340', 8999.00, 50, '13.4英寸轻薄本，英特尔酷睿Ultra，OLED屏幕', 2, 0),
('联想拯救者Y9000P', '笔记本', '联想', 'Y9000P-2024', 9999.00, 80, '16英寸游戏本，i9-14900HX，RTX4070', 1, 0),
('罗技MX Master 3S', '配件', '罗技', 'MX Master 3S', 699.00, 200, '无线蓝牙鼠标，静音点击，8K DPI', 3, 0),
('罗技G Pro X键盘', '配件', '罗技', 'G Pro X', 1299.00, 150, '机械键盘，热插拔轴体，RGB背光', 3, 0);
