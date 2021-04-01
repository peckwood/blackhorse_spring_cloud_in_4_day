## Feign

Netflix开发的声明式, 模板化的http客户端

- Feign可帮助我们更加便捷，优雅的调用HTTP API。
- 在SpringCloud中，使用Feign非常简单——创建一个接口，并在接口上添加一些注解，代码就完
  成了。
- Feign支持多种注解，例如Feign自带的注解或者JAX-RS注解等。
- SpringCloud对Feign进行了增强，使Feign支持了SpringMVC注解，并整合了Ribbon和Eureka，
  从而让Feign的使用更加方便。  

### 项目: spring_cloud_demo_06_hello_feign

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

