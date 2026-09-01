package com.shop.computershop.mapper;

import com.shop.computershop.entity.Admin;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {
    Admin findById(@Param("adminId") Integer adminId);
    Admin findByAdminName(@Param("adminName") String adminName);
    int insert(Admin admin);
    int update(Admin admin);
}
