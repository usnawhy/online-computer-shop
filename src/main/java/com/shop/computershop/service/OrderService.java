package com.shop.computershop.service;

import com.shop.computershop.entity.Orders;
import com.shop.computershop.entity.OrderItem;
import com.shop.computershop.entity.Product;
import com.shop.computershop.mapper.OrderMapper;
import com.shop.computershop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 * 核心: 下单时使用乐观锁扣减库存，防止超卖
 */
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductService productService;

    public Orders findById(Integer orderId) {
        Orders order = orderMapper.findById(orderId);
        if (order != null) {
            order.setOrderItems(orderMapper.findItemsByOrderId(orderId));
        }
        return order;
    }

    public List<Orders> findByUserId(Integer userId) {
        return orderMapper.findByUserId(userId);
    }

    public List<Orders> findAll() {
        return orderMapper.findAll();
    }

    /**
     * 创建订单（事务）
     * 1. 生成订单编号
     * 2. 计算总金额
     * 3. 乐观锁扣减库存（防止超卖）
     * 4. 插入订单和订单项
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Integer userId, List<OrderItem> items,
                              String receiverName, String receiverPhone, String receiverAddress) {
        // 1. 计算总金额和总数
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;
        for (OrderItem item : items) {
            Product product = productMapper.findById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + item.getProductId());
            }
            item.setProductName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(new BigDecimal(item.getCount())));
            totalAmount = totalAmount.add(item.getSubtotal());
            totalCount += item.getCount();

            // 2. 乐观锁扣减库存
            boolean success = productService.decreaseStock(item.getProductId(), item.getCount());
            if (!success) {
                throw new RuntimeException("商品[" + product.getProductName() + "]库存不足，请重试");
            }
        }

        // 3. 构建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setTotalCount(totalCount);
        order.setStatus("待付款");
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setCreateTime(new Date());

        // 4. 插入订单
        orderMapper.insert(order);

        // 5. 插入订单项
        for (OrderItem item : items) {
            item.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(item);
        }

        return order;
    }

    /**
     * 生成订单编号: 时间戳 + UUID前6位
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()) + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public int updateStatus(Integer orderId, String status) {
        return orderMapper.updateStatus(orderId, status);
    }
}
