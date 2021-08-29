package org.example.r2dbc.repo;

import org.example.r2dbc.R2dbcApplication;
import org.example.r2dbc.conf.R2dbcCustomConversionsConfig;
import org.example.r2dbc.conf.converter.TypeWriteConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

/**
 * @author renc
 */
@DataR2dbcTest
@ContextConfiguration(classes = {
        R2dbcApplication.class,
        R2dbcCustomConversionsConfig.class,
        TypeWriteConverter.class})
class PersonRepositoryTest {

    @Autowired
    private PersonRepository repository;

    @Test
    void init() {
        Assertions.assertNotNull(repository);
    }

    @Test
    void readsAllEntitiesCorrectly() {
        repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void setFixedFirstnameFor() {
        repository.setFixedFirstnameByNameParam("1", "Mike")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

}