package org.example.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RestController
    static class TestController {

        private Random r = new Random(47);

        @PostMapping("/test")
        public Mono<String> test() throws InterruptedException {
            int i = r.nextInt(10) + 10;
            System.out.println("SLEEP: " + i);
            TimeUnit.SECONDS.sleep(i);
            return Mono.just(">>>> OK");
        }
    }
}
