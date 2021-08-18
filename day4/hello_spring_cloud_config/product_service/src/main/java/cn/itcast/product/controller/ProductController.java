package cn.itcast.product.controller;

import cn.itcast.product.entity.Product;
import cn.itcast.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product")
public class ProductController{
    @Autowired
    private ProductService productService;

    @Value("${name}")
    private String name;

    @GetMapping("{id}")
    public Product findById(@PathVariable Long id){
        return productService.findById(id);
    }

    @RequestMapping("test")
    public String test(){
        return name;
    }



}
