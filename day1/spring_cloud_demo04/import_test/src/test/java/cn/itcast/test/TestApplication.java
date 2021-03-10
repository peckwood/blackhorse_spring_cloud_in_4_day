package cn.itcast.test;

import cn.itcast.bean.EnableUserBean;
import cn.itcast.bean.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@EnableUserBean
public class TestApplication{
    /**
     * --> EnableUserBean -> User
     * @param args
     */
    public static void main(String[] args){
        //获取Spring容器
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestApplication.class);
        User bean = ac.getBean(User.class);
        System.out.println(bean);

    }
}
