package cn.itcast.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
// 开启Zuul网关功能
@EnableZuulProxy
public class ZuulServerApplication{
    public static void main(String[] args){
        SpringApplication.run(ZuulServerApplication.class, args);
    }
}
