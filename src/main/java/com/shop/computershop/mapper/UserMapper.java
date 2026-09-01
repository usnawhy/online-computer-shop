package com.shop.computershop.mapper;

import com.shop.computershop.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    User findById(@Param("userId") Integer userId);
    User findByUsername(@Param("username") String username);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int deleteById(@Param("userId") Integer userId);
    // 登录验证
    User login(@Param("username") String username, @Param("password") String password);
}
