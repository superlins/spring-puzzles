// package org.example.gateway.filter;
//
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
// import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
// import org.springframework.stereotype.Component;
// import reactor.core.publisher.Mono;
//
// /**
//  * @author renc
//  */
// @Component
// public class RewriteBodyGatewayFilterFactory<C> extends AbstractBodyRewriteGatewayFilterFactory<C> {
//
//     @Override
//     public GatewayFilter apply(C config) {
//         return new OrderedGatewayFilter((exchange, chain) -> {
//             return rewrite(exchange, chain, requestTrans(), responseTrans())
//                     .doOnError(ex -> ex.printStackTrace());
//         }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 10);
//     }
//
//     private RewriteFunction<byte[],byte[]> responseTrans() {
//         return (exchange, bytes) -> Mono.just(bytes);
//     }
//
//     private RewriteFunction<byte[], byte[]> requestTrans() {
//         return (exchange, bytes) -> Mono.just(bytes);
//     }
// }
