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

### Zuul的缺陷

- 性能问题

  Zuul1x版本本质上就是一个同步Servlet，采用多线程阻塞模型进行请求转发。简单讲，每来
  一个请求，Servlet容器要为该请求分配一个线程专门负责处理这个请求，直到响应返回客户
  端这个线程才会被释放返回容器线程池。如果后台服务调用比较耗时，那么这个线程就会被
  阻塞，阻塞期间线程资源被占用，不能干其它事情。我们知道**Servlet容器线程池的大小是有**
  **限制的，当前端请求量大，而后台慢服务比较多时，很容易耗尽容器线程池内的线程，造成**
  **容器无法接受新的请求**。

- 不支持任何长连接，如websocket  

# Spring Cloud Gateway

## 路由配置

### 项目: spring_cloud_demo_gateway

#### 搭建环境

1. 创建工程, 导入坐标

   ```xml
   <!--spring cloud gateway内部是通过netty + webflux实现-->
   <!--webflux和springmvc存在冲突-->
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-gateway</artifactId>
   </dependency>
   ```

2. 配置启动类

   ```java
   @SpringBootApplication
   public class GatewayServerApplication{
       public static void main(String[] args){
           SpringApplication.run(GatewayServerApplication.class, args);
       }
   }
   ```

3. 编写配置文件

   ```yaml
   spring:
     cloud:
       #配置Spring Cloud Gateway的路由
       gateway:
         routes:
           #配置路由: 路由id, 路由到微服务的uri, 断言(判断条件)
           - id: product-service
             uri: http://127.0.0.1:9001
             predicates:
               # 注意与zuul不同, Path里的内容会全部被append到uri的后面, 所以不能随便写, 必须是product
               - Path=/product/**
   ```

`http://127.0.0.1:9001` + `/product/**`

### 路由规则

断言: 路由条件

### 动态路由(面向服务的路由)

1. pom添加eureka client依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
   </dependency>
   ```

2. 在`application.xml`内修改路径方式

   ```yaml
   spring:
     cloud:
       #配置Spring Cloud Gateway的路由
       gateway:
         routes:
           #配置路由: 路由id, 路由到微服务的uri, 断言(判断条件)
           - id: product-service # 保持唯一即可
             # uri: http://127.0.0.1:9001 # 目标微服务请求地址
             uri: lb://service-product # 根据微服务名称从注册中心中拉取服务请求路径
             predicates:
            # 注意与zuul不同, Path里的内容会全部被append到uri的后面, 所以不能随便写, Path里面必须是product
               - Path=/product/** #转发路由条件 Path: 路径匹配条件
   ```

动态路由的限制: 只能用被访问的微服务内定义的路径, 比如product就必须是`/product/**`, 不能用`product-service/product/**`

1. 他是

### 路径重写

可以用路径重写的方式, 实现`product-service/product/**`

通过路由过滤器, 按照配置的正则的规则, 将`product-service`去掉

```yaml
spring:
  cloud:
    #配置Spring Cloud Gateway的路由
    gateway:
      routes:
        #配置路由: 路由id, 路由到微服务的uri, 断言(判断条件)
        - id: product-service # 保持唯一即可
#          uri: http://127.0.0.1:9001 # 目标微服务请求地址
          uri: lb://service-product # 根据微服务名称从注册中心中拉取服务请求路径
          predicates:
            # 注意与zuul不同, Path里的内容会全部被append到uri的后面, 所以不能随便写, Path里面必须是product
#            - Path=/product/** #转发路由条件 Path: 路径匹配条件
            # 上一种方式不允许自定义路径, 如果要实现自定义路径, 如localhost:8080/product-service/product/:id
            # 需要重写转发路径
            - Path=/product-service/**
          filters: #配置路由过滤器
            - RewritePath=/product-service/(?<segment>.*), /$\{segment} #路径重写的过滤器, 在yml中$写为$\
```

try with `localhost:8080/product-service/product/1`

### 根据微服务名称转发

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true #开启
          lower-case-service-id: true #小写
```

try with `localhost:8080/service-product/product/1` and `localhost:8080/service-order/order/buy/1`

## 过滤器

### 过滤器基础

#### 过滤器的生命周期

- pre 请求被路由调用前执行, 可以用来实现身份验证, 集群中选择请求的微服务, 记录调试信息
- post 调用微服务后执行, 可以用来为响应添加标准的HTTP header等

#### 过滤器类型

- GatewayFilter 应用到单个路由, 或者一个分组的路由上
- GlobalFilter 应用到所有的路由上

#### 局部过滤器

每个过滤器工厂都对应一个实现类，并且这些类的名称必须以 GatewayFilterFactory 结尾，这是
Spring Cloud Gateway的一个约定，例如 AddRequestHeader 对应的实现类为
AddRequestHeaderGatewayFilterFactory 。对于这些过滤器的使用方式可以参考官方文档  

### 全局过滤器

Spring Cloud Gateway定义了GlobalFilter接口, 用户可以自定义实现自己的GlobalFilter.

可以实现对权限的统一校验, 安全性验证

## 统一鉴权

```java
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        System.out.println("执行了自定义的全局过滤器");
        String token = exchange.getRequest().getQueryParams().getFirst("access-token");
        if (StringUtils.hasText(token)) {
            // 继续向下执行
            return chain.filter(exchange);
        }else{
            System.out.println("没有登陆");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
```

- 自定义全局过滤器需要实现GlobalFilter和Ordered接口。
- 在filter方法中完成过滤器的逻辑判断处理
- 在getOrder方法指定此过滤器的优先级，返回值越大级别越低
- ServerWebExchange 就相当于当前请求和响应的上下文，存放着重要的请求-响应属性、请求实
  例和响应实例等等。一个请求中的request，response都可以通过 ServerWebExchange 获取
  调用 
- chain.filter 继续向下游执行  

## 网关限流

### 基于filter的限流

1. 准备工作

   1. redis

      1. 启动redis server
      2. 启动redis client, 输入`monitor`监控redis内的数据变化

   2. 工程中引入redis依赖: 基于reative的redis依赖

      ```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-actuator</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
      </dependency>
      ```

2. 请求方式: localhost:8080/service-product/product/1?access-token=1

3. 修改网关中的application.yml

   ```yaml
   spring:
     application:
       name: api-gateway-server
     redis:
       host: localhost
       pool: 6379
       database: 0
     cloud:
       #配置Spring Cloud Gateway的路由
       gateway:
         routes:
           #配置路由: 路由id, 路由到微服务的uri, 断言(判断条件)
           - id: product-service # 保持唯一即可
             # uri: http://127.0.0.1:9001 # 目标微服务请求地址
             uri: lb://service-product # 根据微服务名称从注册中心中拉取服务请求路径
             predicates:
               # 需要重写转发路径
               - Path=/product-service/**
             filters: #配置路由过滤器
               # 使用限流过滤器, 是Spring Cloud Gateway提供的
               - name: RequestRateLimiter
                 args:
                   # 使用SpEL从容器中获取对象
                   # 指定为限流的标准
                   key-resolver: '#{@pathKeyResolver}'
                   # 参数 replenishRate: 向令牌桶中填充的速率
                   redis-rate-limiter.replenishRate: 1
                   # burstCapacity: 令牌桶的容量
                   redis-rate-limiter.burstCapacity: 3
               - RewritePath=/product-service/(?<segment>.*), /$\{segment} #路径重写的过滤器, 在yml中$写为$\
   ```

4. 配置redis中key的解析器KeyResolver

```java
@Bean
public KeyResolver pathKeyResolver(){
    //自定义的KeyResolver
    return new KeyResolver(){
        /**
         *
         * @param exchange 上下文参数
         * @return
         */
        @Override
        public Mono<String> resolve(ServerWebExchange exchange){
            //通过路径限流
            // return Mono.just(exchange.getRequest().getPath().toString());
            // 基于请求ip的限流
            return Mono.just(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));
        }
    };
}
```



### 基于Sentinal的限流

## 网关的高可用

1. 安装nginx for windows `http://nginx.org/en/download.html`

2. edit conf/nginx.conf

   ```
       #集群配置
       upstream gateway{
           server 127.0.0.1:8083;
           server 127.0.0.1:8084;
       }
   
       server {
           listen       80;
           server_name  localhost;
   
           # 127.0.0.1
           location / {
               proxy_pass http://gateway;
           }
        }
   ```
   
   
   
3. start nginx with `start nginx` in executable folder

4. create two GatewayServerApplication configuration, one at 8083, one at 8084.

5. as long as one GatewayServerApplication is running, your requests to `http://localhost/product-service/product/1` will be handled

## 分布式链路追踪

Distrubuted Tracing

分布式链路追踪（Distributed Tracing），就是将一次分布式请求还原成调用链路，进行

- 日志记录
- 性能监控
- 将一次分布式请求的调用情况集中展示。比如各个服务节点上的耗时、请求具体到达哪台机器
  上、每个服务节点的请求状态等等  

我们会学习Sleuth结合twitter的Zipkin的解决方案

### Sleuth 概述

Sleuth主要功能是在分布式系统中提供追踪解决方案

![](https://img.raiden.live/images/2021/05/19/a7ba2c64f5ffdb38c9d0205d443c8b28.png)

1. `47dc85c 2021-05-19 23:47 raiden (HEAD -> master) [spring_cloud_tracking_analysis] 搭建基础项目: gateway访问order, order访问product.`

2. 在所有要打印日志的微服务中(gateway, order, product)添加sleuth依赖

   ```XML
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
   ```
   
3. 给这些微服务的application.yml添加对应的日志配置

   ```yaml
   logging:
     level:
       root: INFO
       org.springframework.web.servlet.DispatcherServlet: DEBUG
       org.springframework.cloud.sleuth: DEBUG
   ```
   
4. 通过gateway,发送请求, 可以在3个微服务的日志中看到类似

   ```
   2021-05-20 19:43:22.194 DEBUG [api-gateway-server,0f6878061326d67e,0f6878061326d67e,false]
   ```
    ```
    2021-05-20 19:43:22.191 DEBUG [service-order,0f6878061326d67e,10914c94b6c30952,false]
    ```
    ```
    2021-05-20 19:43:22.190 DEBUG [service-product,0f6878061326d67e,7d9ca53a3f65c310,false]
    ```


5. 第一个数字(`0f6878061326d67e`)代表追踪的trace id, 第二个代表每一次发起请求对应的span id

查看日志文件并不是一个很好的方法，当微服务越来越多日志文件也会越来越多，通过Zipkin可以将日
志聚合，并进行可视化展示和全文检索。

### Zipkin

![](https://img.raiden.live/images/2021/05/20/9aEqad1Fxc.png)

上图展示了Zipkin地基础架构, 主要由4个核心组件构成

- Collector. 收集微服务发送过来的跟踪数据, 将这些数据转存到Zipkin内部
- Storage. 把信息存入到数据库或内存
- Restful API. 提供外部访问接口
- Web UI. 浏览器内看

Zipkin 分为两端，一个是 Zipkin 服务端，一个是 Zipkin 客户端，客户端也就是微服务的应用。
客户端会配置服务端的 URL 地址，一旦发生服务间的调用的时候，会被配置在微服务里面的 Sleuth 的
监听器监听，并生成相应的 Trace 和 Span 信息发送给服务端。
发送的方式主要有两种，一种是 HTTP 报文的方式，还有一种是消息总线的方式如 RabbitMQ。
不论哪种方式，我们都需要：
一个 Eureka 服务注册中心，这里我们就用之前的 eureka 项目来当注册中心。
一个 Zipkin 服务端。
多个微服务，这些微服务中配置Zipkin 客户端。  

#### Zipkin Server的部署和配置  

1. 在Cygwin或linux里, 创建zipkin自己的文件夹, cd进去后运行

```
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

如果端口被占用了, 所有方法都不行, 试试重启.

2. 给每个微服务配置好zipkin客户端

   1. 给gateway, order, product微服务都加上zipkin依赖
   2. 给它们3个的配置文件application.yml都加上zipkin配置

       ```yaml
       spring:
         zipkin:
           base-url: http://127.0.0.1:9411/ #server的请求路径
           sender:
             type: web #数据的传输方式, 以http的方式向server端发送数据
         sleuth:
           sampler:
             probability: 1 #采样比, 默认是0.1, 开发时可以设置为1
       ```

   

3. 访问http://localhost:9411/zipkin 查看链路信息, 如微服务调用关系, 请求方式, 请求时间等

> https://zipkin.io/pages/quickstart

4. ![zipkin存在的问题和解决](https://img.raiden.live/images/2021/05/22/zipkin.png)

#### zipkin服务端数据保存

因为zipkin默认把链路数据存在内存, 所以重启zipkin服务器会导致旧数据丢失

1. 创建数据库zipkin

   ```mysql
   CREATE DATABASE /*!32312 IF NOT EXISTS*/`zipkin` /*!40100 DEFAULT CHARACTER SET utf8 */;
   USE `zipkin`;
   ```

2. 添加表

   1. 运行https://github.com/openzipkin/zipkin/blob/master/zipkin-storage/mysql-v1/src/main/resources/mysql.sql的sql

3. 修改server的启动命令

   ```
   java -jar zipkin.jar --STORAGE_TYPE=mysql --MYSQL_HOST=127.0.0.1 --MYSQL_TCP_PORT=3306 --MYSQL_USER=root --MYSQL_PASS=root --MYSQL_DB=zipkin
   ```

   > https://github.com/openzipkin/zipkin/blob/master/zipkin-server/src/main/resources/zipkin-server-shared.yml

4. 这样配置之后, 再重启时, 数据不会丢失, 依然可以再web UI里查看

#### 基于消息中间件收集数据  

1. 安装rabbit MQ

   1. 启动后访问http://127.0.0.1:15672/#/, 使用guest/guest登录

2. 修改server配置, 改为访问rabbit MQ

   1. 修改server的启动命令

      ```
      java -jar zipkin.jar --RABBIT_ADDRESSES=127.0.0.1:5672 --RABBIT_USER=guest --RABBIT_PASSWORD=guest
      ```

3. 修改zipkin客户端以rabbit mq的方式发送链路数据

   1. 给对应的3个微服务添加相关依赖

      ```xml
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-sleuth-zipkin</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.amqp</groupId>
          <artifactId>spring-rabbit</artifactId>
      </dependency>
      ```

   2. application.yml添加对应配置

      ```yaml
      spring:
        rabbitmq:
          host: localhost
          port: 5672
          username: guest
          password: guest
          listener: # 这里配置了重试策略
            direct:
              retry:
                enabled: true
            simple:
              retry:
                enabled: true
      ```

   3. 关闭zipkin

   4. 依次启动微服务, 发送请求

   5. 去rabbit mq查看zipkin queue内ready的消息的数量, 应等于发送请求数量

   6. 启动zipkin

   7. 确认刚刚发送的请求能被查询到

