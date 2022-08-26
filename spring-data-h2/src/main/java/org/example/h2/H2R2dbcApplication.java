package org.example.h2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.concurrent.TimeUnit;

/**
 * @author renc
 */
@SpringBootApplication
public class H2R2dbcApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(H2R2dbcApplication.class, args);
        DatabaseClient client = context.getBean(DatabaseClient.class);
        client.sql("select * from user")
                .fetch()
                .all()
                .doOnNext(map -> System.out.println(">>>>>> " + map))
                .subscribe();
        TimeUnit.SECONDS.sleep(3);
    }
}
