package org.example.jpa.domain.model.task;

import org.example.jpa.domain.shard.ValueObject;

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
