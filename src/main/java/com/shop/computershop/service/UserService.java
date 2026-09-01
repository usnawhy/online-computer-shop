package com.shop.computershop.service;

import com.shop.computershop.entity.User;
import com.shop.computershop.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务
 * 使用BCrypt进行密码加密
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    // BCrypt密码加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 用户注册 - 密码使用BCrypt加密
     */
    public int register(User user) {
        // 检查用户名是否已存在
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        // BCrypt加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user);
    }

    /**
     * 用户登录 - BCrypt密码验证
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        // BCrypt验证密码
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public int update(User user) {
        return userMapper.update(user);
    }

    public int deleteById(Integer userId) {
        return userMapper.deleteById(userId);
    }
}
