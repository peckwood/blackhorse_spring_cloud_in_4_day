# Spring Cloud Stream

## 消息中间件

消息中间件主要解决应用解耦，异步消息，流量削锋等问题，实现高性能，高可用，可伸缩和最终一致性架构.

不同的中间件其实现方式，内部结构是不一样的。如常见的RabbitMQ和Kafka，由于这两个消息中间件的架构上的不同，像
RabbitMQ有exchange，kafka有Topic，partitions分区，这些中间件的差异性导致我们实际项目开发
给我们造成了一定的困扰，我们如果用了两个消息队列的其中一种，后面的业务需求，我想往另外一种
消息队列进行迁移，这时候无疑就是一个灾难性的，一大堆东西都要重新推倒重新做，因为它跟我们的
系统耦合了，这时候 springcloud Stream 给我们提供了一种**解耦合**的方式。

## 概述

应用通过Spring Cloud Stream插入的input(相当于消费者consumer，它是从队列中接收消息的)和output(相当于生产者producer，它是从队列中发送消息的。)通道与外界交流。通道通过指定中间件的Binder实现与外部代理连接。业务开发者不再关注具
体消息中间件，只需关注Binder对应用程序提供的抽象概念来使用消息中间件实现业务即可。   

![](https://img.raiden.live/images/2021/05/23/2.png)

### 消息生产者的入门案例

1. 添加依赖

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-stream</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
   </dependency>
   ```

2. 添加配置文件

   ```yaml
   server:
     port: 7001 #服务端口
   spring:
     application:
       name: stream_producer #指定服务名
     rabbitmq:
       addresses: 127.0.0.1
       username: guest
       password: guest
     cloud:
       stream:
         bindings:
           output:
             # 指定消息发送的目的地, 在rabbitmq中, 发送到一个叫itcast-default的exchange
             destination: itcast-default
         binders: # 配置绑定器
           defaultRabbit:
             type: rabbit
   ```

3. ProducerApplication.java:

   ```java
   //通过配置好的Source接口得到MessageChannel
   @EnableBinding(Source.class)
   @SpringBootApplication
   public class ProducerApplication implements CommandLineRunner{
       @Autowired
       private MessageChannel output;
   
   
       @Override
       public void run(String... args) throws Exception{
           //发送消息
           //通过MeesageBuilder工具类创建消息
   
           output.send(MessageBuilder.withPayload("Hello 你好").build());
       }
   
       public static void main(String[] args){
           SpringApplication.run(ProducerApplication.class);
       }
   }
   ```

4. 运行ProducerApplication.java

5. 在rabbit的http://127.0.0.1:15672/#/exchanges界面的exchange列表确认itcast-default的存在, 它的type应该是topic

6. 下图是工作原理

![](https://img.raiden.live/images/2021/05/23/1.png)

### 消息消费者的入门案例

1. 和生产者的开发方式很像
2. 参考`day4/stream_consumer`
3. 测试时, 先运行`ConsumerApplication`, 再运行一次`ProducerApplication`
4. Consumer的terminal会出现`获取到消息: Hello 你好`

