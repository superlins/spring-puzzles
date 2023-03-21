package org.example.ddd.domain.model.task;

import org.example.ddd.domain.shard.ValueObject;

/**
 * @author renc
 */
public enum TaskState implements ValueObject<TaskState> {

    LOADING, RUNNABLE, RUNNING, PAUSED, CANCELED, COMPLETED;

    @Override
    public boolean sameValueAs(TaskState other) {
        return this.equals(other);
    }
}
