package org.example.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * @author renc
 */
@SpringBootApplication
public class R2dbcApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(R2dbcApplication.class, args);
        DatabaseClient client = run.getBean(DatabaseClient.class);
        client.sql("select 1")
                .fetch()
                .one()
                .doOnNext(m -> System.out.println(m));
    }
}
