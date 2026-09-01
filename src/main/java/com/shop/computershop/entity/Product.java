package com.shop.computershop.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体
 * 包含乐观锁字段 version，用于防止超卖
 */
@Data
public class Product {
    private Integer productId;     // 商品ID
    private String productName;    // 商品名称
    private String category;       // 分类: 笔记本/台式机/配件等
    private String brand;          // 品牌
    private String model;          // 型号
    private BigDecimal price;      // 价格
    private Integer stock;         // 库存
    private String description;    // 商品描述
    private String imageUrl;       // 图片URL
    private Integer supplierId;    // 供应商ID
    private Integer status;        // 状态: 0-下架 1-上架
    private Date createTime;       // 创建时间
    private Integer version;       // 乐观锁版本号，防止超卖
}
