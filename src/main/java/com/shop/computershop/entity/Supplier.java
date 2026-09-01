package com.shop.computershop.entity;

import lombok.Data;

/**
 * 供应商实体
 */
@Data
public class Supplier {
    private Integer supplierId;   // 供应商ID
    private String supplierName;  // 供应商名称
    private String contactPerson; // 联系人
    private String phone;         // 联系电话
    private String address;       // 地址
    private String description;   // 描述
}
