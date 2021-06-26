package cn.itcast.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 负责向中间件发送数据
 */
@EnableBinding(Source.class)
public class MessengerSender{
    @Autowired
    private MessageChannel output;

    public void send(Object obj){
        output.send(MessageBuilder.withPayload(obj).build());
    }
}
