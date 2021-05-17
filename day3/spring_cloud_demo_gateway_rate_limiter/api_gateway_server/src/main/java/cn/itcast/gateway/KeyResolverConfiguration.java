package cn.itcast.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfiguration{
    /**
     * 基于请求路径的限流规则
     * 请求URL: /abc
     * 基于请求IP 127.0.0.1
     * 基于参数
     */
    @Bean
    public KeyResolver pathKeyResolver(){
        //自定义的KeyResolver
        return new KeyResolver(){
            /**
             *
             * @param exchange 上下文参数
             * @return
             */
            @Override
            public Mono<String> resolve(ServerWebExchange exchange){
                //通过路径限流
                return Mono.just(exchange.getRequest().getPath().toString());
            }
        };
    }

    /**
     * 基于请求参数的限流
     * 请求URL: abc?userId = 1
     * @return
     */
    // @Bean
    public KeyResolver userKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest().getQueryParams().getFirst("userId")
        );
    }

    /**
     * 基于请求IP的限流, 不好测试, 就只是写在这里了
     * @return
     */
    // @Bean
    public KeyResolver ipKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")
        );
    }



}
