package com.shop.computershop.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项实体(中间表)
 * 解决订单(orders)与商品(product)之间的多对多关系
 * 一个订单可以包含多个商品，一个商品可以出现在多个订单中
 */
@Data
public class OrderItem {
    private Integer itemId;        // 订单项ID
    private Integer orderId;       // 订单ID(外键)
    private Integer productId;     // 商品ID(外键)
    private String productName;    // 商品名称(冗余，下单时快照)
    private BigDecimal price;      // 商品单价(下单时快照)
    private Integer count;         // 购买数量
    private BigDecimal subtotal;   // 小计金额 = price * count
}
