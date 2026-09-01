package com.shop.computershop.entity;

import lombok.Data;
import java.util.Date;

/**
 * 用户实体(买家)
 */
@Data
public class User {
    private Integer userId;       // 用户ID
    private String username;      // 用户名
    private String password;      // 密码(BCrypt加密)
    private String realName;      // 真实姓名
    private String phone;         // 手机号
    private String email;         // 邮箱
    private String address;       // 收货地址
    private Date registerTime;    // 注册时间
    private Integer status;       // 状态: 0-禁用 1-正常
}
