package cn.itcast.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义一个全局过滤器
 * GlobalFilter, Ordered 接口
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered{
    /**
     * 执行过滤器中的业务逻辑
     * 对请求参数中的access-token进行判断
     * 如果存在此参数, 代表认证成功
     * 如果不存在, 认证失败
     * ServerWebExchange: 相当于请求和响应的上下文(Zuul中的RequestContext)
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        System.out.println("执行了自定义的全局过滤器");
        String token = exchange.getRequest().getQueryParams().getFirst("access-token");
        if (StringUtils.hasText(token)) {
            // 继续向下执行
            return chain.filter(exchange);
        }else{
            System.out.println("没有登陆");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 指定过滤器的执行顺序, 返回值越小, 执行优先级越高
     * @return
     */
    @Override
    public int getOrder(){
        return 0;
    }
}
