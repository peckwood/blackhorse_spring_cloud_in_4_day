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

