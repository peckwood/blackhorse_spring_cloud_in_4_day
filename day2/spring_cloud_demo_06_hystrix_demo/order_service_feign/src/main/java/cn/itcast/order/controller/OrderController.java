package cn.itcast.order.controller;

import cn.itcast.order.entity.Product;
import cn.itcast.order.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController{

    @Autowired
    private ProductFeignClient productFeignClient;

    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        return productFeignClient.findById(id);
    }
}
