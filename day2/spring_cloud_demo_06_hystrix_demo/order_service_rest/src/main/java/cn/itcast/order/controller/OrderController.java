package cn.itcast.order.controller;

import cn.itcast.order.entity.Product;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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
     * 使用注解配置熔点保护
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "orderFallback")
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        // 调用discoveryClient方法
        //以调用服务名称获取所有的元数据
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");

        //获取唯一的一个元数据
        ServiceInstance instance = instances.get(0);
        //根据元数据中的主机地址和端口号拼接请求
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/product/" + id;
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }

    /**
     * 降级方法
     * 和需要受到保护的方法的接口参数, 返回值一致
     */
    public Product orderFallback(Long id){
        Product product = new Product();
        product.setProductName("触发降级方法");
        return product;
    }
}
