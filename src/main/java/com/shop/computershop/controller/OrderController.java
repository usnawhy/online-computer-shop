package com.shop.computershop.controller;

import com.shop.computershop.entity.OrderItem;
import com.shop.computershop.entity.Orders;
import com.shop.computershop.service.OrderService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/get")
    public Orders get(@RequestParam Integer orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/user")
    public List<Orders> userOrders(@RequestParam Integer userId) {
        return orderService.findByUserId(userId);
    }

    @GetMapping("/list")
    public List<Orders> list() {
        return orderService.findAll();
    }

    /**
     * 创建订单
     * 包含乐观锁库存扣减，防止超卖
     */
    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            String receiverName = (String) params.get("receiverName");
            String receiverPhone = (String) params.get("receiverPhone");
            String receiverAddress = (String) params.get("receiverAddress");
            @SuppressWarnings("unchecked")
            List<OrderItem> items = (List<OrderItem>) params.get("items");

            Orders order = orderService.createOrder(userId, items, receiverName, receiverPhone, receiverAddress);
            return Map.of("code", 200, "msg", "下单成功", "data", order);
        } catch (Exception e) {
            return Map.of("code", 500, "msg", e.getMessage());
        }
    }

    @PostMapping("/status")
    public String updateStatus(@RequestParam Integer orderId, @RequestParam String status) {
        orderService.updateStatus(orderId, status);
        return "订单状态更新成功";
    }
}
