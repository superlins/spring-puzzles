package org.example.mvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * @author renc
 */
@RestController
public class DemoController {

    private static final Random _R = new Random(47);

    @PostMapping("/api/100febc9")
    public ResponseEntity<Object> listLevel(@RequestBody Map<String, Object> map) {
        requireNonNull(map.get("name"), "the name is required");
        requireNonNull(map.get("idcardno"), "the idcardno is required");
        requireNonNull(map.get("phone"), "the phone is required");

        Map<String, Object> m = new HashMap<>();
        m.put("level", _R.nextInt(5) + 1);

        return ResponseEntity.ok(Result.ok(m));
    }

    static final class Result<T> {
        private int code;
        private T data;
        private String message;

        private Result() {
        }

        private Result(int code, T data, String message) {
            this.code = code;
            this.data = data;
            this.message = message;
        }

        public static <T> Result<T> ok(T data) {
            return new Result<>(200, data, null);
        }

        public static <T> Result<T> error(String msg) {
            return new Result<>(500, null, msg);
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
