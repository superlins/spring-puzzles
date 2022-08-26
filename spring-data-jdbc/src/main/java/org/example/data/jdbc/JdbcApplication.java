package org.example.data.jdbc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author renc
 */
@SpringBootApplication
public class JdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdbcApplication.class, args);
    }

    @Component
    class TestRunner implements CommandLineRunner {

        private final JdbcTemplate jdbcTemplate;

        public TestRunner(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @Override
        public void run(String... args) throws Exception {
            List<String> strings = Files.readAllLines(Paths.get("/Users/renc/iCoder/IdeaProjects/spring-puzzles/spring-data-jdbc/src/main/resources/content.txt"));
            int size = strings.size();
            int record = (int) Math.ceil(size / 3);

            List<Object[]> objects = new ArrayList<>();

            for (int i = 0; i < size; i+=3) {
                String id = UUID.randomUUID().toString().replace("-", "");
                String version = strings.get(i);
                String name = strings.get(i+1);
                String fields = strings.get(i+2);
                objects.add(new Object[]{id, name, version, fields});
            }

            String sql = "insert into DET_PRODUCT_VERSION (id, product_name, product_version_no, product_fields) values (?, ?, ?, ?)";
            jdbcTemplate.batchUpdate(sql, objects);
        }
    }
}
