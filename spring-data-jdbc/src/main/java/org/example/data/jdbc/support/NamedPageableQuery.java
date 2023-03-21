package org.example.data.jdbc.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

@Component
public class NamedPageableQuery implements PageableQuery {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final SnakeCaseStrategy SNAKE_CASE = new SnakeCaseStrategy();

    public NamedPageableQuery(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> Page<T> query(String sql, Map<String, Object> params, Pageable pageable, RowMapper<T> rowMapper) {

        String countSQL = "SELECT COUNT(1) FROM (" + sql + ") t";
        Long total = jdbcTemplate.queryForObject(countSQL, params, Long.class);

        if (total == null || total == 0L) {
            return new PageImpl<>(emptyList());
        }

        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            String ordered = sort.stream()
                    .map(order -> SNAKE_CASE.translate(order.getProperty()) + " " + order.getDirection())
                    .collect(joining(","));
            sql += " ORDER BY " + ordered;
        }

        if (pageable.isPaged()) {
            sql += " LIMIT :offset, :size";
            params.put("offset", pageable.getOffset());
            params.put("size", pageable.getPageSize());
        }


        List<T> query = jdbcTemplate.query(sql, params, rowMapper);

        return new PageImpl<>(query, pageable, total);
    }
}

