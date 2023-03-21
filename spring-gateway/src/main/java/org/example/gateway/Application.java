package org.example.gateway;

import org.example.gateway.filter.ExchangeGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("TEST-SERVICE", r -> r.path("/**")
                        .filters(f -> f
                                .retry(config -> config.setRetries(3)
                                        .setStatuses(HttpStatus.GATEWAY_TIMEOUT, HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.INTERNAL_SERVER_ERROR)
                                        .setMethods(HttpMethod.GET, HttpMethod.POST))
                                // .filter(new ModifyRequestBodyGatewayFilterFactory()
                                //         .apply(config -> config.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                //                 .setInClass(String.class)
                                //                 .setOutClass(String.class)
                                //                 .setRewriteFunction((serverWebExchange, s) -> Mono.just(s + ":U"))), NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 10)
                                .filter(new ExchangeGatewayFilterFactory().apply(new Object()))
                                // .filter(new LoggingGatewayFilterFactory().apply(new Object()))
                        ).uri("https://example.org")).build();
    }
}
