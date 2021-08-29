package org.example.data.jdbc;

import org.example.data.jdbc.domain.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author renc
 */
@JdbcTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActorNamedParameterJdbcTemplateTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void countOfActorsByFirstName() {
        String sql = "select count(*) from t_actor where first_name = :first_name";
        SqlParameterSource namedParameters = new MapSqlParameterSource("first_name", "Yurij");
        int count = this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        assertTrue(count > 0);
    }

    @Test
    void countOfActorsByFirstNameWithMap() {

        String sql = "select count(*) from T_ACTOR where first_name = :first_name";

        Map<String, String> namedParameters = Collections.singletonMap("first_name", "Yurij");
        int count = this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        assertTrue(count > 0);
    }

    @Test
    void countOfActorsWithPOJO() {
        String sql = "select count(*) from t_actor where first_name = :firstName and last_name = :lastName";

        Actor exampleActor = new Actor();
        exampleActor.setFirstName("Leonor");
        exampleActor.setLastName("Watling");
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(exampleActor);

        int count = this.namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        assertTrue(count > 0);
    }

    @Test
    void batchUpdateWithParameterSource() {
        List<Actor> actors = new ArrayList<>();
        // ...
        this.namedParameterJdbcTemplate.batchUpdate(
                "update t_actor set first_name = :firstName, last_name = :lastName where id = :id",
                SqlParameterSourceUtils.createBatch(actors));
    }
}
