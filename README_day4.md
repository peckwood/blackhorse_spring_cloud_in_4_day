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

### 基于入门案例的代码优化

视频: 08-基于入门案例的代码优化.avi

企业开发当中, 没人把生产消息写到启动类当中, 我们需要抽取

1. `ProducerApplication`内的关于消息的配置被抽取到了`cn.itcast.stream.MessengerSender`工具类内
2. `cn.itcast.stream.ConsumerApplication`内关于MessageListener的配置被抽取到了`cn.itcast.stream.consumer.MessageListener`类内
3. 运行:
   1. 运行cn.itcast.stream.ConsumerApplication, 启动Consumer, 开始监听消息
   2. 运行`cn.itcast.stream.ProducerTest#testSend`方法, 使用工具类MessengerSender发送消息
   3. ConsumerApplication的console会打印出`获取到消息: hello 工具类`

### 自定义消息通道

对应视频: 09-自定义消息通道.avi

![](https://img.raiden.live/images/2021/05/23/1.png)

##### 逻辑(producer)

1. myoutput.send()
2. output是MessageChannel
3. 调`output.send()`的类要加注解`@EnableBinding(MyProcessor.class)`
4. MyProcessor里包含了关于output的定义
5. output的destination在配置文件中配置`itcase-custom-output`

#### 逻辑(consumer)

1. 实现`MyMessageListener`
2. `MyMessageListener`也有注解`@EnableBinding(MyProcessor.class)`, 使用`@StreamListener`加载是MessageChannel类的myinput
3. input的destination在配置文件中配置`itcase-custom-output`

#### 运行

1. 同优化后的运行方式
2. 注意要运行`MyProducerTest`里面的@Test方法

### 消息分组

视频: 10-消息分组.avi

1. 把module`stream_consumer`复制了一份`stream_consumer_2`, 端口改为7003
2. 开启`ConsumerApplication`和`ConsumerApplication2`
3. 使用`ProducerTest`或`MyProducerTest`发送一条消息
4. 2个ConsuemrApplication都接收到了此消息, 因为他们的目的地destination是一样的

>当同一个服务启动多个实例的时候，这些实例都会绑定到同一个消息通道的目标主题（Topic）上  

如果我们希望生产者发送的消息只被其中一个消费者实例消费, 我们需要给他们设置分组

我们修改2个consumer的配置为

```yaml
spring:
  cloud:
    stream:
      bindings:
        myinput:
          destination: itcase-custom-output
          group: group1 #设置消息的组名称(同名组中的多个消费者, 只有一个会消费消息)
```

1. 重新启动2个consumer
2. 重新调用MyProducerTest发送消息
3. 发现只有一个consumer接收到了消息
