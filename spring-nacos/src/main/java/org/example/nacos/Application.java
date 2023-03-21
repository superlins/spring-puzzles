package org.example.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        while(true) {
            String userConf = applicationContext.getEnvironment().getProperty("user.config");
            String userAge = applicationContext.getEnvironment().getProperty("user.age");
            String userTest = applicationContext.getEnvironment().getProperty("user.test");
            System.err.println("user conf :" + userConf + "; age: " + userAge);
            System.err.println("user test :" + userTest);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
