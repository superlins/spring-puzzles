package org.example.data.jdbc.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;

import java.util.Map;

/**
 * @author renc
 */
public interface PageableQuery {

    <T> Page<T> query(String sql, Map<String, Object> params, Pageable pageable, RowMapper<T> rowMapper);
}

