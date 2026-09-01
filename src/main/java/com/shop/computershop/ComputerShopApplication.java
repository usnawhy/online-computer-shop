package com.shop.computershop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在线电脑商城系统启动类
 * 数据库课程设计 - 2025.12
 */
@SpringBootApplication
@MapperScan("com.shop.computershop.mapper")
public class ComputerShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComputerShopApplication.class, args);
        System.out.println("=== 在线电脑商城系统启动成功 ===");
    }
}
