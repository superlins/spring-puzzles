package org.example.actuator;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        System.setProperty("reactor.netty.ioWorkerCount", "1");
        SpringApplication.run(Application.class, args);
    }

    // ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    //
    // @Bean
    // public ApplicationRunner applicationRunner(MeterRegistry meterRegistry) {
    //     return args -> {
    //         Counter counter = Counter.builder("dashboard.counter")
    //                 .baseUnit(BaseUnits.EVENTS)
    //                 .description("Number of error events that kafka consume action")
    //                 .register(meterRegistry);
    //         scheduledExecutorService.scheduleWithFixedDelay(() -> {
    //             counter.increment();
    //         }, 500, 1000, TimeUnit.MILLISECONDS);
    //     };
    // }

    @RestController
    public class HelloController {

        @Value("${server.port}")
        private Integer port;

        @Autowired
        private WebClient.Builder webclientBuilder;

        @Autowired
        private LoadBalancedExchangeFilterFunction loadBalancedExchangeFilterFunction;

        // @Counted(value = "test.counted",
        //         extraTags = {"ip", "172.16.250.11", "env", "test"},
        //         description = "Number of test() method invoked")
        @RequestMapping("/hello")
        public Mono<String> test(ServerHttpRequest serverHttpRequest) {
            // ExchangeStrategies codecs = ExchangeStrategies.builder()
            //         .codecs(configurer -> configurer
            //         .defaultCodecs()
            //         .maxInMemorySize(16 * 1024 * 1024))
            //         .build();
            // WebClient webClient = webclientBuilder.exchangeStrategies(codecs).build();

            WebClient webClient = webclientBuilder.build();
            Mono<String> resp = webClient.get()
                    .uri("http://localhost:8081/ac/mock")
                    .retrieve()
                    .bodyToMono(String.class)
                    .log();

            return resp;
        }

        @RequestMapping("/mock")
        public Mono<String> mock() {
            return Mono.just("OK");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class MicrometerAspectConfiguration {

        @Bean
        public CountedAspect countedAspect(MeterRegistry meterRegistry) {
            return new CountedAspect(meterRegistry);
        }
    }
}
