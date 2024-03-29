package cn.itcast.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MyProducerTest{
    @Autowired
    private MyMessengerSender myMessengerSender;

    @Test
    public void testSend(){
        myMessengerSender.send("hello from myMessengerSender");
    }

    @Test
    public void testSendMultipleTimes(){
        for(int i=0;i<5;i++){
            myMessengerSender.send("1");
        }
    }
}
