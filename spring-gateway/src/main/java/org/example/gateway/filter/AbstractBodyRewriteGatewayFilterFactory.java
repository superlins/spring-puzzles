// package org.example.gateway.filter;
//
// import org.reactivestreams.Publisher;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
// import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
// import org.springframework.cloud.gateway.support.BodyInserterContext;
// import org.springframework.core.io.buffer.DataBuffer;
// import org.springframework.core.io.buffer.DataBufferFactory;
// import org.springframework.core.io.buffer.DataBufferUtils;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ReactiveHttpOutputMessage;
// import org.springframework.http.codec.HttpMessageReader;
// import org.springframework.http.codec.HttpMessageWriter;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
// import org.springframework.web.reactive.function.BodyInserters;
// import org.springframework.web.reactive.function.client.ClientResponse;
// import org.springframework.web.reactive.function.server.HandlerStrategies;
// import org.springframework.web.reactive.function.server.ServerRequest;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import java.util.List;
// import java.util.function.Function;
// import java.util.function.Supplier;
//
// import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
//
// /**
//  * Abstract gateway filter factory that modifies the request and/or the response body.
//  *
//  * @author renc
//  * @see HttpMessageReader
//  * @see HttpMessageWriter
//  */
// public abstract class AbstractBodyRewriteGatewayFilterFactory<C> extends AbstractGatewayFilterFactory<C> {
//
//     protected final List<HttpMessageReader<?>> messageReaders;
//
//     public AbstractBodyRewriteGatewayFilterFactory() {
//         this.messageReaders = HandlerStrategies.withDefaults().messageReaders();
//     }
//
//     public AbstractBodyRewriteGatewayFilterFactory(Class<C> configClass) {
//         super(configClass);
//         this.messageReaders = HandlerStrategies.withDefaults().messageReaders();
//     }
//
//     public AbstractBodyRewriteGatewayFilterFactory(List<HttpMessageReader<?>> messageReaders, Class<C> configClass) {
//         super(configClass);
//         this.messageReaders = messageReaders;
//     }
//
//     /**
//      * Rewrite {@link ServerHttpRequest Request} according a request transformer as {@link RewriteFunction}.
//      */
//     protected Mono<Void> rewriteRequest(ServerWebExchange exchange, GatewayFilterChain chain, RewriteFunction<byte[], byte[]> requestTrans) {
//
//         ReactiveCachedBodyOutputMessage outputMessage = new ReactiveCachedBodyOutputMessage(exchange, contentLenHeaderRemoved(exchange));
//
//         return BodyInserters.fromPublisher(modifiedRequestBody(exchange, requestTrans), byte[].class)
//                 .insert(outputMessage, new BodyInserterContext())
//                 .then(Mono.defer(() -> {
//                     ServerHttpRequest serverHttpRequest = serverHttpRequest(exchange, outputMessage);
//                     return chain.filter(exchange.mutate().request(serverHttpRequest).build());
//                 })).onErrorResume(th -> release(outputMessage, th));
//     }
//
//     /**
//      * Rewrite {@link ServerHttpResponse Response} according a response transformer as {@link RewriteFunction}.
//      */
//     protected Mono<Void> rewriteResponse(ServerWebExchange exchange, GatewayFilterChain chain, RewriteFunction<byte[], byte[]> responseTrans) {
//         return chain.filter(exchange.mutate().response(serverHttpResponse(exchange, responseTrans)).build());
//     }
//
//     protected Mono<Void> rewrite(ServerWebExchange exchange, GatewayFilterChain chain,
//              RewriteFunction<byte[], byte[]> requestTrans, RewriteFunction<byte[], byte[]> responseTrans) {
//
//         ReactiveCachedBodyOutputMessage outputMessage = new ReactiveCachedBodyOutputMessage(exchange, contentLenHeaderRemoved(exchange));
//
//         return BodyInserters.fromPublisher(modifiedRequestBody(exchange, requestTrans), byte[].class)
//                 .insert(outputMessage, new BodyInserterContext())
//                 .then(Mono.defer(() -> {
//                     ServerHttpRequest serverHttpRequest = serverHttpRequest(exchange, outputMessage);
//                     ServerHttpResponse serverHttpResponse = serverHttpResponse(exchange, responseTrans);
//                     return chain.filter(exchange.mutate()
//                             .request(serverHttpRequest)
//                             .response(serverHttpResponse)
//                             .build());
//                 })).onErrorResume(th -> release(outputMessage, th));
//     }
//
//     private Mono<Void> release(ReactiveCachedBodyOutputMessage outputMessage, Throwable throwable) {
//         // if (outputMessage.isCached()) {
//         //     return outputMessage.getBody().map(DataBufferUtils::release).then(Mono.error(throwable));
//         // }
//         return Mono.error(throwable);
//     }
//
//     private HttpHeaders contentLenHeaderRemoved(ServerWebExchange exchange) {
//         HttpHeaders headers = new HttpHeaders();
//         headers.putAll(exchange.getRequest().getHeaders());
//         headers.remove(HttpHeaders.CONTENT_LENGTH);
//         return headers;
//     }
//
//     private Mono<byte[]> modifiedRequestBody(ServerWebExchange exchange, RewriteFunction<byte[], byte[]> requestTrans) {
//         return ServerRequest.create(exchange, messageReaders).bodyToMono(byte[].class)
//                 .flatMap(bytes -> (Mono<byte[]>) requestTrans.apply(exchange, bytes))
//                 .switchIfEmpty(Mono.defer(() -> (Mono) requestTrans.apply(exchange, null)));
//     }
//
//     protected ServerHttpRequest serverHttpRequest(ServerWebExchange exchange, ReactiveCachedBodyOutputMessage outputMessage) {
//         return new ServerHttpRequestDecorator(exchange.getRequest()) {
//             @Override
//             public HttpHeaders getHeaders() {
//                 HttpHeaders httpHeaders = new HttpHeaders();
//                 httpHeaders.putAll(outputMessage.getHeaders());
//                 return httpHeaders;
//             }
//
//             @Override
//             public Flux<DataBuffer> getBody() {
//                 return outputMessage.getBody();
//             }
//         };
//     }
//
//     protected ServerHttpResponse serverHttpResponse(ServerWebExchange exchange, RewriteFunction<byte[], byte[]> rewriteFunction) {
//         return new ServerHttpResponseDecorator(exchange.getResponse()) {
//
//             @Override
//             public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                 String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
//                 HttpHeaders httpHeaders = new HttpHeaders();
//                 httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
//
//                 ClientResponse clientResponse = ClientResponse.create(exchange.getResponse().getStatusCode(), messageReaders)
//                         .headers(headers -> headers.putAll(httpHeaders))
//                         .body(Flux.from(body))
//                         .build();
//
//                 Mono<byte[]> modifiedBody = clientResponse.bodyToMono(byte[].class)
//                         .flatMap(source -> (Mono<byte[]>) rewriteFunction.apply(exchange, source))
//                         .switchIfEmpty(Mono.defer(() -> (Mono) rewriteFunction.apply(exchange, null)));
//
//                 ReactiveCachedBodyOutputMessage outputMessage = new ReactiveCachedBodyOutputMessage(exchange,
//                         exchange.getResponse().getHeaders());
//
//                 return BodyInserters.fromPublisher(modifiedBody, byte[].class)
//                         .insert(outputMessage, new BodyInserterContext())
//                         .then(Mono.defer(() -> {
//                             Mono<DataBuffer> messageBody = DataBufferUtils.join(outputMessage.getBody());
//                             HttpHeaders headers = getDelegate().getHeaders();
//                             if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)
//                                     || headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
//                                 messageBody = messageBody.doOnNext(data -> headers.setContentLength(data.readableByteCount()));
//                             }
//                             return getDelegate().writeWith(messageBody);
//                         }));
//             }
//
//             @Override
//             public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
//                 return writeWith(Flux.from(body).flatMapSequential(Function.identity()));
//             }
//         };
//     }
//
//     protected class ReactiveCachedBodyOutputMessage implements ReactiveHttpOutputMessage {
//
//         private final DataBufferFactory bufferFactory;
//         private final HttpHeaders httpHeaders;
//         private boolean cached = false;
//         private Flux<DataBuffer> body = Flux.error(new IllegalStateException("The body is not set. Did handling complete with success?"));
//
//         public ReactiveCachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders) {
//             this.bufferFactory = exchange.getResponse().bufferFactory();
//             this.httpHeaders = httpHeaders;
//         }
//
//         public void beforeCommit(Supplier<? extends Mono<Void>> action) {
//         }
//
//         public boolean isCommitted() {
//             return false;
//         }
//
//         public boolean isCached() {
//             return this.cached;
//         }
//
//         public HttpHeaders getHeaders() {
//             return this.httpHeaders;
//         }
//
//         public DataBufferFactory bufferFactory() {
//             return this.bufferFactory;
//         }
//
//         public Flux<DataBuffer> getBody() {
//             return this.body;
//         }
//
//         public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//             this.body = Flux.from(body);
//             this.cached = true;
//             return Mono.empty();
//         }
//
//         public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
//             return this.writeWith(Flux.from(body).flatMap(Function.identity()));
//         }
//
//         public Mono<Void> setComplete() {
//             return this.writeWith(Flux.empty());
//         }
//     }
// }
