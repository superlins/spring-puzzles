// package org.example.gateway.filter;
//
// import org.reactivestreams.Publisher;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
// import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
// import org.springframework.core.io.buffer.DataBuffer;
// import org.springframework.core.io.buffer.NettyDataBuffer;
// import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
// import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import java.nio.charset.StandardCharsets;
//
// import static org.springframework.core.io.buffer.DataBufferUtils.join;
//
// @Component
// public class SendTraceMessageGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
//
//     @Override
//     public GatewayFilter apply(Object config) {
//         return new OrderedGatewayFilter((exchange, chain) -> {
//             ServerHttpRequestDecorator serverHttpRequestDecorator = requestDecorator(exchange);
//             ServerHttpResponseDecorator serverHttpResponseDecorator = responseDecorator(exchange);
//             ServerWebExchange serverWebExchange = exchange.mutate()
//                     .request(serverHttpRequestDecorator)
//                     .response(serverHttpResponseDecorator)
//                     .build();
//
//             return chain.filter(serverWebExchange);
//         }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1);
//     }
//
//     private ServerHttpRequestDecorator requestDecorator(ServerWebExchange exchange) {
//         return new ServerHttpRequestDecorator(exchange.getRequest()) {
//             @Override
//             public Flux<DataBuffer> getBody() {
//                 Mono<DataBuffer> dataBufferMono = join(super.getBody())
//                         .doOnNext(buffer -> {
//                             System.out.println("REQ: " + buffer.toString(StandardCharsets.UTF_8));
//                             System.out.println("REF_CNT: " + ((NettyDataBuffer)buffer).getNativeBuffer().refCnt());
//                         });
//                 return Flux.from(dataBufferMono);
//             }
//         };
//     }
//
//     private ServerHttpResponseDecorator responseDecorator(ServerWebExchange exchange) {
//         return new ServerHttpResponseDecorator(exchange.getResponse()) {
//             @Override
//             public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                 return super.writeWith(body);
//             }
//         };
//     }
// }