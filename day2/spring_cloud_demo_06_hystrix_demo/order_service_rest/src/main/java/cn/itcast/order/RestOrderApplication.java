package cn.itcast.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

// 激活hystrix
@EnableCircuitBreaker
@SpringBootApplication
@EntityScan("cn.itcast.order.entity")
@EnableDiscoveryClient
public class RestOrderApplication{
    public static void main(String[] args){
        SpringApplication.run(RestOrderApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
