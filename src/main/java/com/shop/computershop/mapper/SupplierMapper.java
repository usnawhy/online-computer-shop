package com.shop.computershop.mapper;

import com.shop.computershop.entity.Supplier;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SupplierMapper {
    Supplier findById(@Param("supplierId") Integer supplierId);
    List<Supplier> findAll();
    int insert(Supplier supplier);
    int update(Supplier supplier);
    int deleteById(@Param("supplierId") Integer supplierId);
}
