package cn.itcast.order.feign;

import cn.itcast.order.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 声明需要调用的微服务名称, "service-product"为product的spring.application.name
 * fallback: 熔断降级发生的降级方法的实现类
 */
@FeignClient(name = "service-product", fallback = ProductFeignClientCallback.class)
public interface ProductFeignClient{
    /**
     * 配置需要调用的微服务接口
     */
    @RequestMapping(value = "product/{id}", method = RequestMethod.GET)
    Product findById(@PathVariable("id") Long id);
}
