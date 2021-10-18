package org.example.actuator;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {


    public static void main(String[] args) {
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

        // @Counted(value = "test.counted",
        //         extraTags = {"ip", "172.16.250.11", "env", "test"},
        //         description = "Number of test() method invoked")
        @RequestMapping("/hello")
        public Mono<String> test() {
            return Mono.just("Hello World!");
            // return Mono.error(new RuntimeException("Just test error"));

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
