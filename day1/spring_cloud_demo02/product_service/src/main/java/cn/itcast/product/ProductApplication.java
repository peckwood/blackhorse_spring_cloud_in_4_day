package cn.itcast.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EntityScan("cn.itcast.product.entity")
@EnableDiscoveryClient
public class ProductApplication{
    public static void main(String[] args){
        SpringApplication.run(ProductApplication.class, args);
    }
}
