package cn.itcast.order.feign;

import cn.itcast.order.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignClientCallback implements ProductFeignClient{
    @Override
    public Product findById(Long id){
        Product product = new Product();
        product.setProductName("feign调用触发熔断降级");
        return product;
    }
}
