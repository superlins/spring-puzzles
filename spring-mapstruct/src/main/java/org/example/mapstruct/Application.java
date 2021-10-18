package org.example.mapstruct;

import org.example.mapstruct.dto.UserRequest;
import org.example.mapstruct.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner(ConversionService conversionService) {
        return args -> {
            UserRequest userRequest = new UserRequest();
            userRequest.setName("张三");
            userRequest.setSex("M");
            userRequest.setAge(10);
            userRequest.setProvince("北京");
            userRequest.setCity("北京");
            userRequest.setArea("丰台");
            userRequest.setInteresting("游戏");
            userRequest.setStreet("淮坊街道");
            User user = conversionService.convert(userRequest, User.class);
            System.out.println(user);
        };
    }
}
