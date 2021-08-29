package org.example.r2dbc;

import org.example.r2dbc.conf.R2dbcCustomConversionsConfig;
import org.example.r2dbc.conf.converter.TypeWriteConverter;
import org.example.r2dbc.domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

/**
 * @author renc
 */
@DataR2dbcTest
@ContextConfiguration(classes = {
        R2dbcApplication.class,
        R2dbcCustomConversionsConfig.class,
        TypeWriteConverter.class})
class ActorR2dbcEntityTemplateTest {

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void init() {
        Assertions.assertNotNull(template);
        template.getDatabaseClient().sql("CREATE TABLE person" +
                        "(id VARCHAR(255) PRIMARY KEY," +
                        "name VARCHAR(255)," +
                        "age INT)")
                .fetch()
                .rowsUpdated()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_select() {
        template.select(Person.class)
                .first()
                .doOnNext(it -> System.out.println(it))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_insert() {
        template.insert(Person.class)
                .using(new Person("joe3", "Joe", 34))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void test_insert_with_instance() {
        Person person = new Person("joe3", "Joe", 34);

        Mono<Person> saved = template.insert(person);
        Mono<Person> loaded = template.selectOne(query(where("name").is("Joe")), Person.class);
    }

    @Test
    void test_complex_select() {
        Mono<Person> first = template.select(Person.class)
                .from("person")
                .matching(query(where("firstname").is("John")
                        .and("lastname").in("Doe", "White"))
                        .sort(by(desc("id"))))
                .one();
    }

    @Test
    void test_update() {
        Person person = new Person("joe3", "Joe", 34);

        Mono<Integer> update = template.update(Person.class)
                .inTable("person")
                .matching(query(where("firstname").is("John")))
                .apply(update("age", 42));
    }

    @Test
    void test_delete() {
        Mono<Integer> delete = template.delete(Person.class)
                .from("other_table")
                .matching(query(where("firstname").is("John")))
                .all();
    }
}
