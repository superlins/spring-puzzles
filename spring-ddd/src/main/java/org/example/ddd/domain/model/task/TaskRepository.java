package org.example.ddd.domain.model.task;

import java.util.List;

/**
 * @author renc
 */
public interface TaskRepository {

    void save(Task t);

    List<Task> findAll();
}
