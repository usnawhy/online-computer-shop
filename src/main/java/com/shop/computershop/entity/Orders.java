package com.shop.computershop.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体
 * 一个订单包含多个订单项(order_item)
 */
@Data
public class Orders {
    private Integer orderId;           // 订单ID
    private String orderNo;            // 订单编号
    private Integer userId;            // 用户ID
    private BigDecimal totalAmount;    // 订单总金额
    private Integer totalCount;        // 商品总数
    private String status;             // 订单状态: 待付款/已付款/已发货/已完成/已取消
    private String receiverName;       // 收货人姓名
    private String receiverPhone;      // 收货人电话
    private String receiverAddress;    // 收货地址
    private Date createTime;           // 创建时间
    private Date payTime;              // 支付时间
    private Date deliverTime;          // 发货时间
    private Date finishTime;           // 完成时间

    private List<OrderItem> orderItems; // 订单项列表(一对多)
}
