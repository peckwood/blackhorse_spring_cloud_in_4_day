package cn.itcast.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProducerTest{
    @Autowired
    private MessengerSender messengerSender;

    @Test
    public void testSend(){
        messengerSender.send("hello 工具类");
    }
}
