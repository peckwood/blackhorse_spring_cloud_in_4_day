package cn.itcast.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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
     *  身份认证:
     *  1. 所有的请求需要携带一个参数access-token
     *  2. 获取request请求
     *  3. 通过request获取参数access-token
     *  4. 判断token是否为空
     *  5.1 token==null, 拦截请求, 返回认证失败
     *  5.2 token!=null, 执行后续操作
     *  在zuul网关中, 通过RequestContext的上下文对象, 可以获取request对象
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException{
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String token = request.getParameter("access-token");
        if(token==null){
            //拦截请求
            currentContext.setSendZuulResponse(false);
            //返回401
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        // 执行后续操作
        return null;
    }
}
