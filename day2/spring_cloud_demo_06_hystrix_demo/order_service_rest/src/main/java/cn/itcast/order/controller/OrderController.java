package cn.itcast.order.controller;

import cn.itcast.order.entity.Product;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @DefaultProperties 指定此接口中公共的熔断设置
 * 如果在@DefaultProperties中指定了公共的降级方法
 * 在@HystrixCommand不需要单独指定了
 *
 * 注意如果使用统一的公共的降级方法, 类中的接口的返回的对象类型应保持一致(在本例中都是Product)
 */
@DefaultProperties(defaultFallback = "defaultFallback")
@RestController
@RequestMapping("order")
public class OrderController{
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 使用注解配置熔点保护
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "orderFallback")
    // @HystrixCommand
    @GetMapping("buy/{id}")
    public Product findProductById(@PathVariable Long id){
        if(id != 1){
            throw new RuntimeException("服务器异常");
        }
        return restTemplate.getForObject("http://localhost:9001/product/1", Product.class);
    }

    /**
     * 指定统一的降级方法
     * 不能有接收参数
     * @return
     */
    public Product defaultFallback(){
        Product product = new Product();
        product.setProductName("触发统一降级方法");
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
