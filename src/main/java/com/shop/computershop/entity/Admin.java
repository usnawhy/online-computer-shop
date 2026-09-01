package com.shop.computershop.entity;

import lombok.Data;
import java.util.Date;

/**
 * 管理员实体
 */
@Data
public class Admin {
    private Integer adminId;      // 管理员ID
    private String adminName;     // 管理员用户名
    private String password;      // 密码(BCrypt加密)
    private String realName;      // 真实姓名
    private String phone;         // 手机号
    private Date createTime;      // 创建时间
}
