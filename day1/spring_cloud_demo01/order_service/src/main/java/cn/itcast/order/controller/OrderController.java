package cn.itcast.order.controller;

import cn.itcast.order.entity.Product;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController{
    /**
     * 通过订单系统
     * 根据product id 查询product
     * @param id product id
     */
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        return null;
    }
}
