package cn.itcast.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

/**
 * 自定义的Zuul过滤器
 * 继承抽象父类
 */
@Component
public class LoginFilter extends ZuulFilter{
    /**
     * 定义过滤器类型, 以下4个中的一个
     * pre
     * routing
     * post
     * error
     * @return
     */
    @Override
    public String filterType(){
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 指定过滤器的执行顺序
     *  返回值越小, 执行顺序越高
     * @return
     */
    @Override
    public int filterOrder(){
        return 1;
    }

    /**
     * 当前过滤器是否生效
     * @return
     */
    @Override
    public boolean shouldFilter(){
        return true;
    }

    /**
     * 指定过滤器中的业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException{
        System.out.println("执行了过滤器");
        return null;
    }
}
