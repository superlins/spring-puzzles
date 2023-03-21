package org.example.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.springframework.core.io.buffer.DataBufferUtils.join;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequestDecorator serverHttpRequestDecorator = requestDecorator(exchange);
            ServerHttpResponseDecorator serverHttpResponseDecorator = responseDecorator(exchange);
            ServerWebExchange serverWebExchange = exchange.mutate()
                    .request(serverHttpRequestDecorator)
                    .response(serverHttpResponseDecorator)
                    .build();
            return chain.filter(serverWebExchange);
        }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1);
    }

    private ServerHttpRequestDecorator requestDecorator(ServerWebExchange exchange) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.from(join(super.getBody())
                        .doOnNext(buffer -> {
                            System.out.println("IN-BODY");
                            System.out.println(buffer.toString(StandardCharsets.UTF_8));
                        }));
            }
        };
    }

    private ServerHttpResponseDecorator responseDecorator(ServerWebExchange exchange) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return super.writeWith(join(body)
                        .doOnNext(buffer -> {
                            System.out.println("OUT-BODY");
                            System.out.println(buffer.toString(StandardCharsets.UTF_8));
                        }));
            }
        };
    }
}