package cn.itcast.order.controller;

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
     * 基于ribbon的形式调用远程微服务
     * 1.使用@LoadBalanced声明RestTemplate
     * 2. 使用服务名称替换ip地址
     */
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        // 调用discoveryClient方法
        //以调用服务名称获取所有的元数据
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");

        //获取唯一的一个元数据
        ServiceInstance instance = instances.get(0);
        //根据元数据中的主机地址和端口号拼接请求
        String url = "http://service-product/product/" + id;
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
}
