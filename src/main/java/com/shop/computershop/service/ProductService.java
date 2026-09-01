package com.shop.computershop.service;

import com.shop.computershop.entity.Product;
import com.shop.computershop.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductService {

    @Resource
    private ProductMapper productMapper;

    public Product findById(Integer productId) {
        return productMapper.findById(productId);
    }

    public List<Product> findAll() {
        return productMapper.findAll();
    }

    public List<Product> findByCategory(String category) {
        return productMapper.findByCategory(category);
    }

    public List<Product> search(String keyword) {
        return productMapper.findByKeyword(keyword);
    }

    public int add(Product product) {
        return productMapper.insert(product);
    }

    public int update(Product product) {
        return productMapper.update(product);
    }

    public int deleteById(Integer productId) {
        return productMapper.deleteById(productId);
    }

    /**
     * 乐观锁扣减库存
     * 先查询商品获取当前version，再带version条件更新
     * 如果version不匹配(被其他线程修改)，更新失败，防止超卖
     */
    public boolean decreaseStock(Integer productId, Integer count) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() < count) {
            throw new RuntimeException("库存不足");
        }
        // 乐观锁更新: WHERE product_id=? AND version=?
        int rows = productMapper.decreaseStock(productId, count, product.getVersion());
        return rows > 0; // rows=0表示version不匹配，并发冲突
    }
}
