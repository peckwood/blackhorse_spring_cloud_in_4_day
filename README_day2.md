## Feign

Netflix开发的声明式, 模板化的http客户端

- Feign可帮助我们更加便捷，优雅的调用HTTP API。
- 在SpringCloud中，使用Feign非常简单——创建一个接口，并在接口上添加一些注解，代码就完
  成了。
- Feign支持多种注解，例如Feign自带的注解或者JAX-RS注解等。
- SpringCloud对Feign进行了增强，使Feign支持了SpringMVC注解，并整合了Ribbon和Eureka，
  从而让Feign的使用更加方便。  

### 项目: spring_cloud_demo_06_hello_feign

#### 对应视频:

02-feign：概述以及入门案例的搭建-上 

到 06-feign：打印fegin日志

Feign组件入门

1. 导入依赖

   ```xml
   <!--spring cloud整合的openfeign-->
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-openfeign</artifactId>
   </dependency>
   ```

   

2. 配置调用接口

   ```java
   /**
    * 声明需要调用的微服务名称, "service-product"为product的spring.application.name
    */
   @FeignClient(name = "service-product")
   public interface ProductFeignClient{
       /**
        * 配置需要调用的微服务接口
        */
       @RequestMapping(value = "product/{id}", method = RequestMethod.GET)
       public Product findById(@PathVariable("id") Long id);
   }
   ```

3. 在启动类上激活Feign

   ```java
   //激活Feign
   @EnableFeignClients
   public class OrderApplication{
   ```

4. 通过自定义的接口调用远程微服务

   ```java
   @Autowired
   private ProductFeignClient productFeignClient;
   
   @GetMapping("buy/{id}")
   public Product findProductById(@PathVariable Long id){
   Product product = productFeignClient.findById(id);
   return product;
   }
   ```

   

Ribbon可以用来调用远程微服务, 但它不是最佳的选择

### Feign和Ribbon的联系

Ribbon是一个基于HTTP的客户端负载均衡的工具, 可以使用RestTemplate模拟http请求, 步骤相当**繁琐**

Feign是在Ribbon的基础上进行了一次改进, 是使用起来更加方便地http客户端, 采用接口的方式, 只要创建一个接口, 在上边添加注解即可.将需要调用的其他服务的方法定义成抽象方法即可, 不需要自己构建http请求. 然后就像是调用自身工程的方法调用, 而感觉不到是调用远程方法, 使得编写客户端非常容易.

### 负载均衡

Feign中本身已经集成了Ribbon依赖和自动配置，因此我们不需要额外引入依赖，也不需要再注册
RestTemplate 对象。另外，我们可以像上节课中讲的那样去配置Ribbon，可以通过 ribbon.xx 来进
行全局配置。也可以通过 服务名.ribbon.xx 来对指定服务配置：
启动两个 shop_service_product ，重新测试可以发现使用Ribbon的轮询策略进行负载均衡  

### 配置日志

```yaml
feign:
  client:
    config:
      # 要请求的服务, 即配置在@FeignClient name字段内的值
      service-product:
#        NONE 不输出日志, 性能最高
#        BASIC 适用于生产环境追踪问题
#        HEADERS 在BASIC的基础上, 记录请求和响应头
#        FULL 记录所有
        loggerLevel: FULL
logging:
  level:
    cn.itcast.order.feign.ProductFeignClient: debug
```

### 源码分析

![](https://img.raiden.live/images/2021/04/01/1569491525364.png)

# Hystrix

### 项目: spring_cloud_demo_06_hystrix_demo part1

注意: part1中的order中只改动了order_service, 其它的order, 如`order_service_rest`和`order_service_feign`, 会在之后的部分使用

#### 实现步骤

1. 模拟环境

2. 修改order_service的tomcat最大线程数, 降低其同时能接受的最大线程数

   ```yaml
   server:
     tomcat:
       max-threads: 10 # 设置最大线程数
   ```

3. 手动增加`service-product`的请求返回时间

4. 安装使用jmeter向`127.0.0.1/order/buy/1`发送大量请求12123

5. 调用新添加的另一个接口`127.0.0.1/order/1`, 发现访问时间显著变长, 原因是请求积压, 线程被`下单方法`占用
![请求积压问题](https://img.raiden.live/images/2021/04/03/d8ce1c9de0e71ef3b4064a1cf2341871.png)

6. order service引入hystrix依赖

7. 添加执行命令`cn.itcast.order.command.OrderCommand`

8. 重启order service

9. 重新用jmeter发送大量请求

10. 调用127.0.0.1/order/1`, 发现不受jmeter发送大量请求的接口的影响

### 雪崩效应

服务与服务之间的依赖性，故障会传播，造成连锁反应，会对整个微服务系统造成灾难
性的严重后果，这就是服务故障的“雪崩”效应  

从源头上我们无法完全杜绝雪崩源头的发生，但是雪崩的根
本原因来源于服务之间的强依赖，所以我们可以提前评估，做好**熔断，隔离，限流**。  

### 服务隔离

将系统按照一定的原则划分为若干个服务模块，各个模块之间相对独立，无强依赖。
当有故障发生时，能将问题和影响隔离在某个模块内部，而不扩散风险，不波及其它模块，不影响整体
的系统服务. `spring_cloud_demo_06_hystrix_demo part1`就把`findProductById`和`findById`隔离了, 生产中我们可以按模块隔离

#### 隔离服务的解决方案

1. 线程池隔离
   1. 给重要的或可能被大量访问的方法配置一个专有线程池
   2. 比如给下单方法配置一个5个线程的线程池, 给查询订单方法配一个4个的
2. 信号量隔离
   1. 可以理解为计数器
   2. 设置请求的方法的最大阈值, 即最多能访问人数, 剩下的人直接返回null

### 熔断降级

在互联网系统中，当下游服务因访问压力过大而响应变慢或失败，上游服务为了保护系统整体的可用性，可以暂时切断对下游服务的调用  

所谓降级，就是当某个服务熔断之后，服务器将不再被调用，此时客户端可以自己准备一个本地的fallback回调，返回一个缺省值。 也可以理解为兜底, 即`spring_cloud_demo_06_hystrix_demo part1`项目中的`cn.itcast.order.command.OrderCommand#getFallback`方法

### 服务限流

简单粗暴, 到达阈值就限制

## 对SpringTemplate的支持

### 项目: `spring_cloud_demo_06_hystrix_demo`里的`order_service_rest`

### 实现步骤

1. 引入hystrix依赖`spring-cloud-starter-netflix-hystrix`

2. 在启动类中激活hystrix

   ```
   @EnableCircuitBreaker
   ```

3. 配置熔断触发的降级逻辑

   1. 添加降级方法`cn.itcast.order.controller.OrderController#orderFallback`

      1. 注意返回值和受到保护的方法的请求参数和返回值是一致的

      ```java
          public Product orderFallback(Long id){
              Product product = new Product();
              product.setProductName("触发降级方法");
              return product;
          }
      ```

   2. 将此方法配置在被保护的接口上配置`@HystrixCommand(fallbackMethod = "orderFallback")`

      ```java
      	@HystrixCommand(fallbackMethod = "orderFallback")
          @GetMapping("buy/{id}")
          public Product findProductById(@PathVariable Long id){
              return restTemplate.getForObject("http://localhost:9001/product/1", Product.class);
          }
      ```

      

4. 在需要保护的接口上使用@HystrixCommand配置

5. 正常调用order_service_rest, 2s后返回正常结果

6. 关闭product_service, 再调用, 3s后返回fallback结果