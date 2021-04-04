package cn.itcast.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("cn.itcast.order.entity")
@EnableDiscoveryClient
// 激活Feign
@EnableFeignClients
// 激活Hystrix,
// 在有集成了Hyestrix的Feign时, 光用Hystrix的熔断功能, 可以不加
// 但要查看实时的监控数据, 必须加
@EnableCircuitBreaker
// 激活hystrix的web监控平台
@EnableHystrixDashboard
public class FeignOrderApplication{
    public static void main(String[] args){
        SpringApplication.run(FeignOrderApplication.class, args);
    }
}
