package com.shop.computershop.controller;

import com.shop.computershop.entity.User;
import com.shop.computershop.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        try {
            userService.register(user);
            res.put("code", 200);
            res.put("msg", "注册成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", e.getMessage());
        }
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> res = new HashMap<>();
        User user = userService.login(username, password);
        if (user != null) {
            res.put("code", 200);
            res.put("msg", "登录成功");
            res.put("data", user);
        } else {
            res.put("code", 500);
            res.put("msg", "用户名或密码错误");
        }
        return res;
    }

    @GetMapping("/list")
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/get")
    public User get(@RequestParam Integer userId) {
        return userService.findById(userId);
    }
}
