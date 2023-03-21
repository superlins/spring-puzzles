package org.example.ddd.domain.model.task;

import org.example.ddd.domain.shard.ValueObject;
import org.springframework.util.Assert;

/**
 * @author renc
 */
public class TaskId implements ValueObject<TaskId> {

    private String name;

    private String creator;

    protected TaskId() {
    }

    public TaskId(String name, String creator) {
        Assert.hasText(name, "任务名不能为空");
        Assert.hasText(creator, "任务创建人不能为空");
        this.name = name;
        this.creator = creator;
    }

    public String idString() {
        return name + ":" + creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskId taskId = (TaskId) o;

        return sameValueAs(taskId);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + creator.hashCode();
        return result;
    }

    @Override
    public boolean sameValueAs(TaskId other) {
        if (!name.equals(other.name)) return false;
        return creator.equals(other.creator);
    }

    @Override
    public String toString() {
        return idString();
    }
}
