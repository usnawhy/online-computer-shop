package com.shop.computershop.mapper;

import com.shop.computershop.entity.Orders;
import com.shop.computershop.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    Orders findById(@Param("orderId") Integer orderId);
    List<Orders> findByUserId(@Param("userId") Integer userId);
    List<Orders> findAll();
    int insert(Orders orders);
    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);

    // 订单项操作
    int insertOrderItem(OrderItem item);
    List<OrderItem> findItemsByOrderId(@Param("orderId") Integer orderId);
}
