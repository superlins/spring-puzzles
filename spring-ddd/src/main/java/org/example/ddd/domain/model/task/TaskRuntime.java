package org.example.ddd.domain.model.task;

import org.example.ddd.domain.shard.ValueObject;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * @author renc
 */
public class TaskRuntime implements ValueObject<TaskRuntime> {

    private static final TaskRuntime EMPTY = new TaskRuntime();

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    protected TaskRuntime() {
    }

    private TaskRuntime(LocalDateTime beginTime) {
        Assert.notNull(beginTime, "任务开始时间不能为空");
        this.beginTime = beginTime;
    }

    private TaskRuntime(LocalDateTime beginTime, LocalDateTime endTime) {
        Assert.notNull(beginTime, "任务开始时间不能为空");
        Assert.notNull(endTime, "任务结束时间不能为空");
        Assert.isTrue(!endTime.isBefore(beginTime), "任务结束时间不能早于开始时间");
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public static TaskRuntime emptyTaskRuntime() {
        return EMPTY;
    }

    TaskRuntime transformToRunning() {
        return new TaskRuntime(LocalDateTime.now());
    }

    TaskRuntime transformToComplete() {
        return new TaskRuntime(this.beginTime, LocalDateTime.now());
    }

    public LocalDateTime beginTime() {
        return beginTime;
    }

    public LocalDateTime endTime() {
        return endTime;
    }

    @Override
    public boolean sameValueAs(TaskRuntime other) {
        if (!beginTime.equals(other.beginTime)) return false;
        return endTime.equals(other.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskRuntime that = (TaskRuntime) o;

        return sameValueAs(that);
    }

    @Override
    public int hashCode() {
        int result = beginTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}
