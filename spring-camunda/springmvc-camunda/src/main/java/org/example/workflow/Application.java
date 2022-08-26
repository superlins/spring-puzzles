package org.example.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
        
    }

    // @Configuration
    // static class Tester {
    //
    //     @Autowired
    //     private RuntimeService runtimeService;
    //
    //     @Bean
    //     public CommandLineRunner commandLineRunner() {
    //         return args -> {
    //             List<String> phones = new ArrayList<>();
    //             phones.add("1");
    //             phones.add("2");
    //             phones.add("3");
    //
    //             VariableMap variables = Variables
    //                     .putValue("phones", phones);
    //             ProcessInstanceWithVariables processInstance = (ProcessInstanceWithVariables) runtimeService.startProcessInstanceByKey("Process_1oeqdlb", variables);
    //
    //             VariableMap outVars = processInstance.getVariables();
    //             System.out.println(outVars);
    //         };
    //     }
    // }
}