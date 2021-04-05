# 微服务网关概述

### 微服务网关解决的问题:

不同的微服务一般会有不同的网络地址，客户端在访问这些微服务时必须记住几十甚至几百个地址，如果让客户端直接与各个微服务通讯，可能会有很多问题：

1. 客户端会请求多个不同的服务，需要维护不同的请求地址，增加开发难度
2. 在某些场景下存在跨域请求的问题
3. 加大身份认证的难度，每个微服务需要独立认证  

### 微服务网关

我们需要一个微服务网关，<u>介于客户端与服务器之间的中间层，所有的外部请求都会先经过微服务网关。客户端只需要与网关交互，只知道一个网关地址即可</u>，这样简化了开发还有以下优点：

1. 易于监控
2. 易于认证
3. 减少了客户端与各个微服务之间的交互次数

API网关是一个<u>服务器</u>，是系统对外的<u>唯一</u>入口。为每个客户端提供一个<u>定制的API</u>。API网关方式的核心要点是，所有的<u>客户端和消费端</u>都通过<u>统一的网关</u>接入微服务，在网关层处理所有的**非业务**功能。

### 作用和应用场景

网关具有的职责，如身份验证、监控、负载均衡、缓存、请求分片与管理、静态响应处理。当然，最主要的职责还是与“**外界联系**”  

# Zuul

## 简介

Zuul组件的核心是一系列的过滤器，这些过滤器可以完成以下功能：

- 动态路由：动态将请求路由到不同后端集群
- 压力测试：逐渐增加指向集群的流量，以了解性能
- 负载分配：为每一种负载类型分配对应容量，并弃用超出限定值的请求
- 静态响应处理：边缘位置进行响应，避免转发到内部集群
  身份认证和安全: 识别每一个资源的验证要求，并拒绝那些不符的请求。

Spring Cloud对Zuul进行了整合和增强。  

## 搭建zuul网关服务器

### 项目: spring_cloud_demo_gateway

此项目基于`spring_cloud_demo_06_hello_feign`

1. 创建工程导入坐标

   ```xml
   <dependency>
      <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
   </dependency>
   ```

2. 配置启动类

   ```java
   @SpringBootApplication
   // 开启Zuul网关功能
   @EnableZuulProxy
   public class ZuulServerApplication{
       public static void main(String[] args){
           SpringApplication.run(ZuulServerApplication.class, args);
       }
   }
   ```

3. 配置文件

   ```yaml
   server:
     port: 8080
   spring:
     application:
       name: api-zuul-server
   ```

## 路由

根据请求的URL, 将请求分配到对应地微服务中进行处理

### 基础路由配置

```yaml
zuul:
  routes:
#    以商品微服务为例
    product-server: #路由的id, 可以自定义
      path: /product-service/** #映射路径, 例如: localhost:8080/product-service/abc
      url: http://127.0.0.1:9001 # 映射路径对应的实际微服务
```

### 面向服务的路由配置

1. 添加Eureka client依赖

   ```xml
   <dependency>
      <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
   </dependency>
   ```

2. 开启Eureka客户端服务发现

   ```java
   @EnableDiscoveryClient
   public class ZuulServerApplication{
   ```

3. 在zuul网关服务中配置eureka的注册中心相关信息

   ```yaml
   #配置eureka
   eureka:
     client:
       service-url:
         defaultZone: http://localhost:9000/eureka
       instance:
         prefer-ip-address: true # 使用ip地址注册
   ```

4. 修改路由中的映射配置

```yaml
zuul:
  routes:
#    以商品微服务为例
    product-server: #路由的id, 可以自定义
      path: /product-service/** #映射路径, 例如: localhost:8080/product-service/abc
#      url: service-product # 映射路径对应的实际微服务
      serviceId: service-product # 配置转发的微服务的服务名称
```

### 简化路由配置

1. 如果路由id 和 对应的微服务的serviceId一致的话, 可以略去对应serviceId的配置
2. 如果不指定path, 默认的path为`/服务名/**`, 如order-service的可以通过`localhost:8080/service-order/order/buy/1`来访问, 虽然Zuul内没有给`service-order`配置路由id

## Zuul中的过滤器

![](https://img.raiden.live/images/2021/04/05/filters.png)

pre: 转发到微服务之前执行的过滤器

routing: 在路由请求时执行的过滤器

post: 执行微服务获取返回值之后执行的过滤器

error: 在整个阶段抛出异常的时候执行的过滤器

### 自定义过滤器

day3/spring_cloud_demo_gateway/api_zuul_server/src/main/java/cn/itcast/zuul/filter/LoginFilter.java

### 使用Zuul实现一个简单的身份认证过滤器

![](https://img.raiden.live/images/2021/04/05/7aa4e778848491a53b64b05e3b18c108.png)

实现后, 分别通过带上参数`access-token`和不带进行访问, 发现不带返回401, 带了返回正常结果