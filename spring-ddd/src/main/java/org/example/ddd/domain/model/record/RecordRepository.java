package org.example.ddd.domain.model.record;

import java.util.List;

/**
 * @author renc
 */
public interface RecordRepository {

    void save(Record record);

    List<Record> findAll();
}
