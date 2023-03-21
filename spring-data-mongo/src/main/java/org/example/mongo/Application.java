package org.example.mongo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author renc
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public class Runner implements CommandLineRunner {

        private final WebClient.Builder builder;

        private final ReactiveMongoTemplate reactiveMongoTemplate;

        public Runner(WebClient.Builder builder, ReactiveMongoTemplate reactiveMongoTemplate) {
            this.builder = builder;
            this.reactiveMongoTemplate = reactiveMongoTemplate;
        }

        @Override
        public void run(String... args) throws Exception {

            Stream<String> stream = Files.lines(Paths.get("src/main/resources/md5.txt"));
            Supplier<Stream<String>> supplier = () -> stream;

            int steps = (int) Math.ceil(supplier.get().count() / Double.valueOf(1000));

            Stream.iterate(0, n -> n + 1).limit(steps)
                    .parallel()
                    .forEach(step -> {
                        List<String> md5ids = supplier.get().skip(step * 1000).limit(1000)
                                .collect(Collectors.toList());
                        Query q = Query.query(Criteria.where("md5").in(md5ids));
                        reactiveMongoTemplate.find(q, Map.class, "sha256_md5").subscribe();
                    });

            // Path path = Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/md5.txt");
            // BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // Flux.fromStream(Files.lines(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/hkxd.csv")))
            //         .map(sha256 -> {
            //             Query q = query(where("sha256").is(sha256));
            //             q.fields().include("md5");
            //             return q;
            //         }).flatMap(query -> reactiveMongoTemplate.findOne(query, Map.class, "sha256_md5"))
            //         .subscribe(m -> write(bw, String.valueOf(m.get("md5"))), e -> close(bw), () -> close(bw));

            // BufferedWriter bw = Files.newBufferedWriter(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/res.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // WebClient webClient = builder.build();
            // Flux.fromStream(Files.lines(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/md5.txt")))
            //         .flatMap(phone -> {
            //             return webClient.post()
            //                     .uri("http://10.50.20.7:30003/udfm/api/M-AC-OCM-01")
            //                     .body(BodyInserters.fromValue(new HashMap() {
            //                         {
            //                             put("name", "test");
            //                             put("phone", phone);
            //                             put("idcardno", phone);
            //                         }
            //                     })).retrieve()
            //                     .bodyToMono(String.class);
            //         }).subscribe(m -> write(bw, m), e -> close(bw), () -> close(bw));

            // ObjectMapper om = new ObjectMapper();
            // BufferedWriter bw = Files.newBufferedWriter(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/res-final.csv"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // Flux.fromStream(Files.lines(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-mongo/src/main/resources/res.csv")))
            //         .map(s -> {
            //             try {
            //                 return om.readValue(s, Map.class);
            //             } catch (JsonProcessingException e) {
            //                 e.printStackTrace();
            //             }
            //             return new HashMap();
            //         }).map(m -> m.get("phone") + "," + m.get("lr_hkxd_b"))
            //         .subscribe(m -> write(bw, m), e -> close(bw), () -> close(bw));
        }
    }

    private void write(BufferedWriter bw, String string){
        try {
            bw.write(string);
            bw.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void close(Closeable closeable){
        try {
            closeable.close();
            System.out.println("Closed the resource");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
