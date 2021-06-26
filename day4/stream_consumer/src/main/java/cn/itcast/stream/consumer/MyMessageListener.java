package cn.itcast.stream.consumer;

import cn.itcast.stream.channel.MyProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

@EnableBinding(MyProcessor.class)
@Component
public class MyMessageListener{
    //监听binding中的消息
    //必须和EnableBinding写到同一个类内
    @StreamListener(MyProcessor.MYINPUT)
    public void input(String message){
        System.out.println("获取到消息: " + message);
    }
}
