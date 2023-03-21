package org.example.ddd.domain.model.task;

import org.example.ddd.domain.shard.Entity;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * @author renc
 */
public class Task implements Entity<Task> {

    /**
     * 任务标识
     */
    private TaskId taskId;

    /**
     * 任务关联数据集标识
     */
    private DatasetId datasetId;

    /**
     * 任务优先级
     */
    private int priority;

    /**
     * 任务状态
     */
    private TaskState taskState;

    /**
     * 任务运行周期
     */
    private TaskRuntime taskRuntime;

    /**
     * 任务关联标注记录表空间
     */
    private TBSpace tbSpace;

    /**
     * 任务锁定者
     */
    private Locker locker;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public Task(TaskId taskId, DatasetId datasetId) {
        Assert.notNull(taskId, "任务唯一标识不能为空");
        Assert.notNull(datasetId, "任务关联数据集不能为空");
        this.taskId = taskId;
        this.datasetId = datasetId;
        this.priority = 5;
        this.taskState = TaskState.LOADING;
        this.taskRuntime = TaskRuntime.emptyTaskRuntime();
        this.tbSpace = TBSpace.newTBSpace();
        this.locker = Locker.emptyLocker();
        this.createdAt = LocalDateTime.now();
    }

    public void setTbSpace(TBSpace tbSpace) {
        this.tbSpace = tbSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return sameIdentityAs(task);
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }

    @Override
    public boolean sameIdentityAs(Task other) {
        return other != null && taskId.equals(other.taskId);
    }

    @Override
    public String toString() {
        return taskId.toString();
    }

    Task() {}

    private Long id;
}
