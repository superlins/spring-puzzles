package org.example.gateway.filter;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author renc
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpReceivedWebFilter implements WebFilter {

    public static final String GATEWAY_REQUEST_RECEIVED_TIME_ATTR =
            ServerWebExchangeUtils.class.getName() + ".gatewayRequestReceivedTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println(">>> received time: " + LocalDateTime.now());
        Mono<Void> filter = chain.filter(exchange);
        exchange.getResponse().getHeaders().add("rsp", LocalDateTime.now().toString());
        return filter;
    }
}
