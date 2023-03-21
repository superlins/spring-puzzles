package org.example.webflux.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author renc
 */
@RestController
public class ApiController {

    /**
     * 黑名单
     */
    @RequestMapping("/api/{code}")
    public Mono<Response<?>> api(@PathVariable("code") String code) {
        Map<String, Object> m = new HashMap<>();
        switch (code) {
            case "a0a1":
                m.put("denied", false);
                break;
            case "a0a2":
                m.put("zwzt", "1");
                break;
            case "a0a3":
                m.put("zwsc", "1");
                break;
            case "a0a4":
                m.put("sysyz", "1");
                break;
            case "a0a5":
                m.put("lxzhf", 300);
                break;
            case "a0a6":
                m.put("wzxypf", 500);
                break;
            case "a0a7":
                m.put("ghhlwpf", "400");
                break;
        }
        return Mono.just(Response.ok(m));
    }

    static class Response<T> {
        private int code;
        private T data;
        private String message;

        public Response(int code, T data) {
            this.code = code;
            this.data = data;
        }

        public Response(int code, T data, String message) {
            this.code = code;
            this.data = data;
            this.message = message;
        }

        public static <T> Response<T> ok(T data) {
            return new Response<>(200, data);
        }

        public int getCode() {
            return code;
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }
}