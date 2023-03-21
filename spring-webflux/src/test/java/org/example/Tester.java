package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * @author renc
 */
public class Tester {

    public static void main(String[] args) {
        Flux.just(1, 2, 3, 4, 5)
                .flatMap(i -> Mono.just(i * i))
                .doOnNext(i -> {
                    System.out.println(i + " " + Thread.currentThread());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .doFinally(sig -> {
                    System.out.println(sig + " " + Thread.currentThread());
                }).subscribe();
    }
}
