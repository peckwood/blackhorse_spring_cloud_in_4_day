package cn.itcast.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 入门案例
 * 1. 引入依赖
 * 2. 配置application.yml文件
 * 3. 发送消息的话, 定义一个通道接口, 通过接口中内置的message channel发送消息
 *    spring cloud stream内置接口Source
 * 4. @EnableBinding: 绑定对应的通道
 * 5. 发送消息的话, 通过MessageChannel发送消息
 *      如果需要Message Channel --> 通过绑定内置接口获取
 */
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
