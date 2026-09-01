package com.shop.computershop.controller;

import com.shop.computershop.entity.Product;
import com.shop.computershop.service.ProductService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping("/list")
    public List<Product> list() {
        return productService.findAll();
    }

    @GetMapping("/get")
    public Product get(@RequestParam Integer productId) {
        return productService.findById(productId);
    }

    @GetMapping("/category")
    public List<Product> category(@RequestParam String category) {
        return productService.findByCategory(category);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword) {
        return productService.search(keyword);
    }

    @PostMapping("/add")
    public String add(@RequestBody Product product) {
        productService.add(product);
        return "新增商品成功";
    }

    @PostMapping("/update")
    public String update(@RequestBody Product product) {
        productService.update(product);
        return "修改商品成功";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Integer productId) {
        productService.deleteById(productId);
        return "删除商品成功";
    }
}
