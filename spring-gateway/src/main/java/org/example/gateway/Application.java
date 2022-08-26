package org.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Controller
    public class TestController {

        private final GatewayControllerEndpoint gatewayControllerEndpoint;

        public TestController(GatewayControllerEndpoint gatewayControllerEndpoint) {
            this.gatewayControllerEndpoint = gatewayControllerEndpoint;
        }

        @GetMapping("/dashboard")
        public void dashboard(ServerHttpResponse serverHttpResponse) {

            // IReactiveDataDriverContextVariable reactiveDataDrivenMode =
            //         new ReactiveDataDriverContextVariable(gatewayControllerEndpoint.routes());
            // model.addAttribute("routes", reactiveDataDrivenMode);
        }
    }
}
