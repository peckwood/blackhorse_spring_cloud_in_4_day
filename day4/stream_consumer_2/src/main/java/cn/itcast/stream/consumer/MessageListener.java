package cn.itcast.stream.consumer;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

@EnableBinding(Sink.class)
@Component
public class MessageListener{
    //监听binding中的消息
    //必须和EnableBinding写到同一个类内
    @StreamListener(Sink.INPUT)
    public void input(String message){
        System.out.println("获取到消息: " + message);
    }
}
