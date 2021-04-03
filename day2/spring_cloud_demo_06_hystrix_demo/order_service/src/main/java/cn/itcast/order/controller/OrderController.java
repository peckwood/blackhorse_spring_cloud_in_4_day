package cn.itcast.order.controller;

import cn.itcast.order.command.OrderCommand;
import cn.itcast.order.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController{
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 注入DiscoveryClient:
     * springcloud提供的获取原数据的工具类
     * 调用方法获取服务的元数据信息
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 使用OrderCommand调用远程服务
     * @param id
     * @return
     */
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        return new OrderCommand(restTemplate, id).execute();
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id){
        return "根据ID查询订单";
    }
}
