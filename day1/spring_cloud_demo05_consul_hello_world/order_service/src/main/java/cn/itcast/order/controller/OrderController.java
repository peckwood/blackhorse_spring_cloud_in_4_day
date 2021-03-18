package cn.itcast.order.controller;

import cn.itcast.order.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("order")
public class OrderController{
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 通过订单系统
     * 根据product id 查询product
     * @param id product id
     */
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        String url = "http://service-product/product/"+ id;
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
}
