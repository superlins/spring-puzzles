package org.example;

import org.example.r2dbc.R2dbcApplication;
import org.example.r2dbc.conf.R2dbcCustomConversionsConfig;
import org.example.r2dbc.conf.converter.PersonReadConverter;
import org.example.r2dbc.conf.converter.PersonWriteConverter;
import org.example.r2dbc.domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.convert.EntityRowMapper;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author renc
 */
@DataR2dbcTest
@ContextConfiguration(classes = {
        R2dbcApplication.class,
        R2dbcCustomConversionsConfig.class,
        PersonReadConverter.class,
        PersonWriteConverter.class})
class ActorDatabaseClientTest {

    @Autowired
    private DatabaseClient client;

    @Autowired
    private R2dbcConverter converter;

    @Test
    void init() {
        Assertions.assertNotNull(client);
        client.sql("CREATE TABLE person (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), age INTEGER);")
                .fetch()
                .first()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void insertPerson() {
        Assertions.assertNotNull(client);
        client.sql("insert into person values (2, 'renc', 11)")
                .fetch()
                .first()
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void test_select() {
        client.sql("SELECT id, name FROM person")
                .fetch()
                .first()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    // TODO(renc): one-to-many test
    @Test
    void test_select_one_to_many() {
        client.sql("select * from employees e left join titles t on e.emp_no = t.emp_no where e.emp_no = '10004'")
                .fetch()
                .all()
                .doOnNext(m -> System.out.println(m))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void test_bind_vars_select() {
        client.sql("SELECT * FROM person WHERE name = :fn")
                // .bind("fn", Parameter.fromOrEmpty("Mike", String.class))
                .bind("fn", Parameter.fromOrEmpty(null, String.class))
                // .bindNull("fn", String.class)
                .fetch()
                .first()
                .doOnNext(p -> System.out.println(p))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_map_select() {
        client.sql("SELECT id, name FROM person WHERE name = :fn")
                .bind("fn", "Joe")
                .map(row -> row.get("id", String.class))
                .first()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_entity_row_map_test() {
        client.sql("SELECT id, name, age FROM person WHERE name = :fn")
                .bind("fn", "Joe")
                .map(new EntityRowMapper<>(Person.class, converter))
                .first()
                .doOnNext(person -> System.out.println(person))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_update() {
        client.sql("UPDATE person SET name = :fn")
                .bind("fn", "Joe")
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_in_select() {
        client.sql("SELECT id, name, state FROM table WHERE age IN (:ages)")
                .bind("ages", Arrays.asList(35, 50))
                .fetch()
                .all()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void test_tuple_in_select() {
        List<Object[]> tuples = new ArrayList<>();
        tuples.add(new Object[] {"John", 35});
        tuples.add(new Object[] {"Ann",  50});

        client.sql("SELECT id, name, state FROM table WHERE (name, age) IN (:tuples)")
                .bind("tuples", tuples)
                .fetch()
                .all()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void test_named_insert() {
        client.sql("INSERT INTO table (name, state) VALUES(:name, :state)")
                .filter((s, next) -> next.execute(s.returnGeneratedValues("id")))
                .bind("name", "")
                .bind("state", "");
    }

    @Test
    void test_stmt_filter_insert() {
        client.sql("INSERT INTO table (name, state) VALUES(:name, :state)")
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map(row -> row.get("id", Integer.class))
                .first();

        client.sql("SELECT id, name, state FROM table")
                .filter(statement -> statement.fetchSize(25));
    }

}
