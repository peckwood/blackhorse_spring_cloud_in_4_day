package cn.itcast.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义一个全局过滤器
 *      GlobalFilter, Ordered 接口
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered{
    /**
     * 执行过滤器中的业务逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        System.out.println("执行了自定义的全局过滤器");
        // 继续向下执行
        return chain.filter(exchange);
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
