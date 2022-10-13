## day1

### preparation

1. start mysql 

   ```
   cd C:\Users\raiden\AppData\Local\Programs\mysql-5.7.38-winx64\bin
    ./mysqld --console
   ```

   

2. 打开heidisql, postman

### 数据库表

展示数据库

### spring_cloud_demo01 模拟微服务环境

模拟微服务环境, 通过RestTemplate调用远程服务

1. 大pom
2. product
   1. pom
   2. application.yml
   3. DAO
   4. CONTROLLER
   5. ProductApplication
3. order
   1. application.yml
   2. controller
4. postman
   1. get product
   2. get product through order

### spring_cloud_demo02 加入EurekaServer

加入EurekaServer, 通过discoveryClient获取微服务的调用路径, 仍用restTemplate调用

1. eureka
   1. pom
   2. yml
   3. EurekaServerApplication
      1. 注解
2. product和order
   1. pom
   2. yml
   3. Application
3. 启动注册中心
4. 启动order, product
5. 看order, product和eureka的console
6. postman
7. 打开http://localhost:9000/查看eureka控制台
8. 关闭product, 控制台移除它
9. 关闭eureka, 重新请求, 仍然可以, 因为缓存了配置

### spring_cloud_demo03 eureka的高可用

eureka的高可用, 这样其中一个eureka宕机后, 另一个可以立即顶上, 不影响注册中心继续提供服务

实现:  运行多个eureka服务, 并互相注册

![](md-img\1569334117905.png)

1. eureka
   1. yml
   2. 创建8000和9000
2. product
   1. yml
3. 运行8000和9000
4. 运行product和order
5. 打开8000和9000的控制台, 互相注册了, 且信息同步

### spring_cloud_demo04 ribbon 客户端负载均衡

ribbon 客户端负载均衡

是 Netflixfa 发布的一个负载均衡器，有助于控制 HTTP 和 TCP客户端行为。在 SpringCloud 中，
Eureka一般配合Ribbon进行使用，Ribbon提供了客户端负载均衡的功能，Ribbon利用从Eureka中读
取到的服务信息，在调用服务节点提供的服务时，会合理的进行负载。

在SpringCloud中可以将注册中心和Ribbon配合使用，Ribbon自动的从注册中心中获取服务提供者的
列表信息，并基于内置的负载均衡算法，请求服务

1. product
   1. controller
2. order
   1. Application @LoadBalanced
   2. controller
3. 开启2个eureka
4. 开启2个product和1个order
5. 开控制台
6. 用postman访问, 确认客户端负载均衡

### spring_cloud_demo05 Hello consul

consul与Eureka的区别
（1）一致性
Consul强一致性（CP）

- 服务注册相比Eureka会稍慢一些。因为Consul的raft协议要求必须过半数的节点都写入成功才认
  为注册成功
- Leader挂掉时，重新选举期间整个consul不可用。保证了强一致性但牺牲了可用性。

Eureka保证高可用和最终一致性（AP）

- 服务注册相对要快，因为不需要等注册信息replicate到其他节点，也不保证注册信息是否
  replicate成功
- 当数据出现不一致时，虽然A, B上的注册信息不完全相同，但每个Eureka节点依然能够正常对外提
  供服务，这会出现查询服务信息时如果请求A查不到，但请求B就能查到。如此保证了可用性但牺
  牲了一致性。

（2）开发语言和使用

eureka就是个servlet程序，跑在servlet容器中
Consul则是go编写而成，安装启动即可

1. 启动consul
   1. `cd C:\Users\raiden\AppData\Local\Programs\consul_1.5.3_windows_amd64`
   2. `./consul agent -dev -client='0.0.0.0'`
2. 进入管理后台页面 http://127.0.0.1:8500/
3. product
   1. pom
   2. yml
4. 启动 product order
5. 查看管理后台
6. postman调用order
7. postman调用 已注册服务
8. 关闭product order
9. 关闭consul
   1. `cd C:\Users\raiden\AppData\Local\Programs\consul_1.5.3_windows_amd64`
   2. `./consul leave`

## day2

### spring_cloud_demo_06_hello_feign

Feign: 声明式服务调用

Netflix开发的声明式, 模板化的http客户端

- Feign可帮助我们更加便捷，优雅的调用HTTP API。
- 在SpringCloud中，使用Feign非常简单——创建一个接口，并在接口上添加一些注解，代码就完 成了。
- Feign支持多种注解，例如Feign自带的注解或者JAX-RS注解等。
- SpringCloud对Feign进行了增强，使Feign支持了SpringMVC注解，并整合了Ribbon和Eureka， 从而让Feign的使用更加方便。

1. order
   1. pom 导入依赖
   2. ProductFeignClient 配置调用接口
   3. OrderApplication 在启动类上激活Feign
   4. OrderController 通过自定义的接口调用远程微服务
2. 启动eureka
3. 启动order product
4. 通过order调用product, 有结果, 并且支持客户端负载均衡

---

Ribbon可以用来调用远程微服务, 但它不是最佳的选择

#### Feign和Ribbon的联系

Ribbon是一个基于HTTP的客户端负载均衡的工具, 可以使用RestTemplate模拟http请求, 步骤相当**繁琐**

Feign是在Ribbon的基础上进行了一次改进, 是使用起来更加方便地http客户端, 采用接口的方式,  只要创建一个接口, 在上边添加注解即可.将需要调用的其他服务的方法定义成抽象方法即可, 不需要自己构建http请求.  然后就像是调用自身工程的方法调用, 而感觉不到是调用远程方法, 使得编写客户端非常容易.

#### 负载均衡

Feign中本身已经集成了Ribbon依赖和自动配置，因此我们不需要额外引入依赖，也不需要再注册 RestTemplate 对象

### spring_cloud_demo_06_hystrix_demo

Hystrix: 客户端容错保护

[实现步骤](https://github.com/peckwood/blackhorse_spring_cloud_in_4_day/blob/master/README_day2.md#%E5%AE%9E%E7%8E%B0%E6%AD%A5%E9%AA%A4)