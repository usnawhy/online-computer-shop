# 在线电脑商城系统

> 数据库系统原理课程设计 | 2025.12

## 项目简介

基于 SpringBoot + MyBatis + MySQL + Vue.js 的在线电脑商城系统，实现了用户注册登录、商品浏览搜索、购物车下单、订单管理等完整电商功能。项目重点在于数据库设计与优化，涵盖表结构设计、多对多关系处理、触发器、视图、索引、乐观锁并发控制等数据库核心技术。

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端框架 | SpringBoot 2.7.18 |
| ORM框架 | MyBatis (XML映射) |
| 数据库 | MySQL 8.0 |
| 密码加密 | BCrypt (Spring Security Crypto) |
| 前端 | Vue.js |
| 工具库 | Lombok |
| JDK版本 | Java 1.8 |
| 构建工具 | Maven |

## 功能模块

### 1. 用户模块
- 用户注册（BCrypt密码加密）
- 用户登录（BCrypt密码验证）
- 用户信息管理

### 2. 商品模块
- 商品列表浏览
- 按分类筛选
- 关键词搜索
- 商品增删改查（管理员）

### 3. 订单模块
- 创建订单（事务 + 乐观锁扣减库存）
- 订单状态流转（待付款→已付款→已发货→已完成）
- 订单取消（触发器自动恢复库存）
- 订单详情查询

### 4. 管理员模块
- 管理员登录
- 商品管理
- 订单管理

## 数据库设计

数据库名：`computer_shop`，共6张表：

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| admin | 管理员表 | admin_id, admin_name, password |
| user | 用户表 | user_id, username, password, phone, address |
| supplier | 供应商表 | supplier_id, supplier_name, contact_person |
| product | 商品表 | product_id, product_name, price, stock, **version(乐观锁)** |
| orders | 订单表 | order_id, order_no, user_id, total_amount, status |
| order_item | 订单项表(中间表) | item_id, order_id, product_id, count, subtotal |

### 多对多关系处理
订单(orders)与商品(product)是**多对多关系**：
- 一个订单可以包含多个商品
- 一个商品可以出现在多个订单中

通过中间表 `order_item` 解决多对多关系，中间表还记录了购买数量、单价快照、小计等业务字段。

## 项目亮点

### 1. 乐观锁防超卖
商品表设计 `version` 字段，扣减库存时使用乐观锁：
```sql
UPDATE product
SET stock = stock - #{count}, version = version + 1
WHERE product_id = #{productId}
  AND version = #{version}    -- 关键：版本号匹配才更新
  AND stock >= #{count}
```
并发场景下，如果 version 被其他线程修改，更新行数为0，扣减失败，有效防止超卖。

### 2. BCrypt密码加密
使用 Spring Security 的 `BCryptPasswordEncoder` 对用户密码进行加密存储和验证，不存储明文密码。

### 3. 触发器
- **trg_after_order_item_insert**：创建订单项后自动扣减库存
- **trg_after_order_cancel**：订单取消时自动恢复库存
- **trg_before_product_insert**：插入商品时自动初始化version

### 4. 视图
- **v_order_detail**：订单详情视图，关联订单、用户信息
- **v_product_sales**：商品销售统计视图，按商品统计销量和销售额
- **v_user_consumption**：用户消费统计视图，统计用户订单数和消费金额

### 5. 索引优化
为高频查询字段创建索引：username、product_name、create_time、order_id+product_id联合索引等，提升查询性能。

### 6. 事务管理
创建订单使用 `@Transactional` 注解，保证库存扣减、订单插入、订单项插入的原子性。

## 项目结构

```
online-computer-shop/
├── src/main/java/com/shop/computershop/
│   ├── ComputerShopApplication.java   # 启动类
│   ├── controller/                     # 控制层
│   │   ├── UserController.java
│   │   ├── ProductController.java
│   │   └── OrderController.java
│   ├── service/                        # 业务层
│   │   ├── UserService.java           # BCrypt加密
│   │   ├── ProductService.java        # 乐观锁扣减库存
│   │   └── OrderService.java          # 事务下单
│   ├── mapper/                         # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── ProductMapper.java
│   │   ├── OrderMapper.java
│   │   ├── AdminMapper.java
│   │   └── SupplierMapper.java
│   └── entity/                         # 实体类
│       ├── User.java
│       ├── Product.java               # 含version乐观锁字段
│       ├── Orders.java
│       ├── OrderItem.java             # 中间表实体
│       ├── Admin.java
│       └── Supplier.java
├── src/main/resources/
│   ├── application.yml
│   ├── mapper/                         # MyBatis XML映射
│   └── sql/schema.sql                  # 数据库脚本(含触发器/视图/索引)
└── pom.xml
```

## 运行方式

1. **创建数据库**：执行 `src/main/resources/sql/schema.sql`
   - 自动创建数据库、6张表、索引、视图、触发器
   - 自动插入初始测试数据

2. **修改配置**：编辑 `application.yml`，将 `password` 改为你的MySQL密码

3. **启动项目**：
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **接口测试**：
   - 用户注册：`POST http://localhost:8081/user/register`
   - 用户登录：`POST http://localhost:8081/user/login?username=user001&password=123456`
   - 商品列表：`GET http://localhost:8081/product/list`
   - 创建订单：`POST http://localhost:8081/order/create`

## 测试账号
- 管理员：admin / 123456
- 用户：user001 / 123456
