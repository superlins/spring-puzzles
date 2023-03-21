package org.example.data.jdbc.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.jdbc.domain.Actor;
import org.example.data.jdbc.domain.Detail;
import org.example.data.jdbc.service.ActorService;
import org.example.data.jdbc.support.PageableQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.hasLength;

/**
 * @author renc
 */
@Service
public class ActorServiceImpl implements ActorService {

    private final PageableQuery pageableQuery;

    public ActorServiceImpl(PageableQuery pageableQuery) {
        this.pageableQuery = pageableQuery;
    }

    @Override
    public Iterable<Actor> actors(Actor actor, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder sbQuery = new StringBuilder("SELECT * FROM t_actor WHERE 1 = 1 ");

        String firstName = actor.getFirstName();
        if (hasLength(firstName)) {
            sbQuery.append("AND first_name LIKE :firstName ");
            params.put("firstName", "%" + firstName + "%");
        }
        String lastName = actor.getLastName();
        if (hasLength(lastName)) {
            sbQuery.append("AND last_name LIKE :lastName ");
            params.put("lastName", "%" + lastName + "%");
        }

        Page<Actor> query = pageableQuery.query(sbQuery.toString(), params, pageable, new RowMapper<Actor>() {
            @Override
            public Actor mapRow(ResultSet rs, int rowNum) throws SQLException {
                Actor newActor = new Actor();

                newActor.setId(rs.getInt("id"));
                newActor.setFirstName(rs.getString("first_name"));
                newActor.setLastName(rs.getString("last_name"));
                newActor.setBirth(rs.getTimestamp("birth").toLocalDateTime());
                newActor.setActive(rs.getBoolean("active"));

                String detail = rs.getString("detail");
                try {
                    newActor.setDetail(new ObjectMapper().readValue(detail, Detail.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                return newActor;
            }
        });
        return query;
    }
}
