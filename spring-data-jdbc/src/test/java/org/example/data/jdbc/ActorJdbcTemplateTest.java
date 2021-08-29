package org.example.data.jdbc;

import org.example.data.jdbc.domain.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author renc
 */
@JdbcTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ActorJdbcTemplateTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void test_count_queryForObject() {
        int rowCount = this.jdbcTemplate.queryForObject("select count(*) from t_actor", Integer.class);
        assertTrue(rowCount > 0);
    }

    @Test
    void test_bind_variable_queryForObject() {
        int countOfActorsNamedJoe = this.jdbcTemplate.queryForObject(
                "select count(*) from t_actor where first_name = ?", Integer.class, "Joe");
        assertTrue(countOfActorsNamedJoe > 0);
    }

    @Test
    void test_resultSet_queryForObject() {
        Actor actor = jdbcTemplate.queryForObject(
                "select first_name, last_name from t_actor where id = ?",
                (resultSet, rowNum) -> {
                    Actor newActor = new Actor();
                    newActor.setFirstName(resultSet.getString("first_name"));
                    newActor.setLastName(resultSet.getString("last_name"));
                    return newActor;
                },
                1212L);
        assertNotNull(actor);
    }

    @Test
    void test_list_resultSet_queryForObject() {
        List<Actor> actors = this.jdbcTemplate.query(
                "select first_name, last_name from t_actor",
                (resultSet, rowNum) -> {
                    Actor actor = new Actor();
                    actor.setFirstName(resultSet.getString("first_name"));
                    actor.setLastName(resultSet.getString("last_name"));
                    return actor;
                });
        assertNotNull(actors);
        assertTrue(actors.size() > 0);
    }

    @Test
    void test_insert() {
        int update = this.jdbcTemplate.update("insert into t_actor (first_name, last_name) values (?, ?)",
                "Leonor", "Watling");
        assertTrue(update > 0);
    }

    @Test
    void test_generate_key() {
        final String INSERT_SQL = "insert into t_actor (first_name) values(?)";
        final String name = "Rob";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] { "id" });
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        // keyHolder.getKey() now contains the generated key
        System.out.println(keyHolder.getKey());
    }

    @Test
    void test_update() {
        int banjo = this.jdbcTemplate.update("update t_actor set last_name = ? where id = ?",
                "Banjo", 5276L);
        assertTrue(banjo > 0);
    }

    @Test
    void test_delete() {
        int update = this.jdbcTemplate.update("delete from t_actor where id = ?",
                Long.valueOf(327678L));
        assertTrue(update > 0);
    }

    @Test
    void test_execute() {
        this.jdbcTemplate.execute("create table mytable (id integer, name varchar(100))");
        this.jdbcTemplate.update("call SUPPORT.REFRESH_ACTORS_SUMMARY(?)", Long.valueOf(1000));
    }

    @Test
    void test_batch_update() {
        List<Actor> actors = new ArrayList<>();
        // ...
        this.jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Actor actor = actors.get(i);
                        ps.setString(1, actor.getFirstName());
                        ps.setString(2, actor.getLastName());
                        ps.setInt(3, actor.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return actors.size();
                    }
                });
    }

    @Test
    void test_batch_with_arrays() {
        List<Actor> actors = new ArrayList<>();
        // ...

        List<Object[]> batch = new ArrayList<Object[]>();
        for (Actor actor : actors) {
            Object[] values = new Object[] {actor.getFirstName(), actor.getLastName(), actor.getId()};
            batch.add(values);
        }
        this.jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?", batch);
    }

    @Test
    void test_batch_with_split() {
        List<Actor> actors = new ArrayList<>();
        // ...
        jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?",
                actors,
                100,
                (PreparedStatement ps, Actor actor) -> {
                    ps.setString(1, actor.getFirstName());
                    ps.setString(2, actor.getLastName());
                    ps.setInt(3, actor.getId());
                });
    }
}