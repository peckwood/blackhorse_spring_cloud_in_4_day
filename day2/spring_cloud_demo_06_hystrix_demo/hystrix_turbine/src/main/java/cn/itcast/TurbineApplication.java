package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

// turbine配置
@EnableTurbine
// hystrix Dashboard也要开启
@EnableHystrixDashboard
@SpringBootApplication
public class TurbineApplication{
    public static void main(String[] args){
        SpringApplication.run(TurbineApplication.class, args);
    }
}
