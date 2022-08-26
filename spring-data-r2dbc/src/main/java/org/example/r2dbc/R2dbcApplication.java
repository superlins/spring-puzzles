package org.example.r2dbc;

import org.example.r2dbc.domain.Actor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
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
        R2dbcEntityTemplate r2dbcEntityTemplate = run.getBean(R2dbcEntityTemplate.class);
        r2dbcEntityTemplate.select(Actor.class)
                .first()
                .subscribe(System.out::println);
    }
}
