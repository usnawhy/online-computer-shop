package com.shop.computershop.mapper;

import com.shop.computershop.entity.Product;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductMapper {
    Product findById(@Param("productId") Integer productId);
    List<Product> findAll();
    List<Product> findByCategory(@Param("category") String category);
    List<Product> findByKeyword(@Param("keyword") String keyword);
    int insert(Product product);
    int update(Product product);
    int deleteById(@Param("productId") Integer productId);

    /**
     * 乐观锁扣减库存，防止超卖
     * 通过 version 字段实现乐观锁
     */
    int decreaseStock(@Param("productId") Integer productId,
                      @Param("count") Integer count,
                      @Param("version") Integer version);
}
