package cn.itcast.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 自定义消息通道
 */
public interface MyProcessor{

    /**
     * 消息生产者的配置
     */
    String MYOUTPUT = "myoutput";

    @Output("myoutput")
    MessageChannel myoutput();

    /**
     * 消息消费者的配置
     */
    String MYINPUT = "myinput";

    @Input("myinput")
    MessageChannel myinput();
}
