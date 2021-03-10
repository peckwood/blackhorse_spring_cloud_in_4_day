## summary

### day1/spring_cloud_demo01

Emulating microservices by creating 2 Spring boot web apps and use RestTemplate to communicate.

通过RestTemplate调用模拟微服务

对应的视频: 

- 10-模拟微服务环境-上
- 11-模拟微服务环境-下
- 12-模拟微服务环境：通过RestTemplate调用远程服务

### spring_cloud_demo02

加入EurekaServer, 通过discoveryClient获取微服务的调用路径, 仍用restTemplate调用

对应的视频: 

- 16-eureka：搭建EurekaServer注册中心
- 17-eureka：将服务注册到注册中心
- 18-eureka：eureka中通过元数据获取微服务的调用路径

## EurekaServer的高可用

​	add these in hosts

```
# for local coding projects
127.0.0.1 peer-1-server.com
127.0.0.1 peer-2-server.com
127.0.0.1 peer-3-server.com
```



1. 准备2个EurekaServer, 需要相互注册
   1. 详见`day1/spring_cloud_demo03/eureka_server/src/main/resources/application.yml`
   2. 注册后, 它们也会互相同步注册信息
2. 需要将微服务注册到2个Eureka server上
   1. `eureka.client.service-url.defaultZone=http://localhost:9000/eureka, http://localhost:8000/eureka`

### spring_cloud_demo03

基于spring_cloud_demo02上修改的Eureka高可用 

EurekaServer的application.yml有2个profile, 需要2个Run Configuration来启动

对应的视频:

- 20-eurekaServer高可用：server间的相互注册
- 21-eurekaServer高可用：服务注册到多个eurekaserver
- 22-eurekaServer高可用：显示IP与服务续约时间设置
- 23-eurekaServer高可用：自我保护机制

## eureka 细节问题

### 在控制台显示服务IP

```
eureka:
	instance:
		prefer-ip-address: true
		instance-id: ${spring.cloud.client.ip-address}-${server.port} #向注册中心注册服务id
```

### Eureka 的服务的剔除问题

client每30s向server发送一次心跳

如果90s内没有发送心跳, 宕机

在服务的提供者, 设置心跳间隔, 设置续约到期时间

```yaml
eureka:
  instance:
#    发送心跳间隔
    lease-renewal-interval-in-seconds: 5
#    续约到期的时间
    lease-expiration-duration-in-seconds: 10
```



### eureka的自我保护机制

在eureka中配置关闭自我保护和提出服务间隔

```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 4000
```

## Ribbon

### 1 服务调用

eureka内部继承了ribbon

- 在创建RestTemplate的时候, 声明@LoadBalanced
- 使用restTemplate调用远程微服务: 不需要拼接微服务的url, 以待请求的服务名替换IP地址



