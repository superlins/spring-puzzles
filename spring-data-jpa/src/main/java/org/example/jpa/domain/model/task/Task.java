package org.example.jpa.domain.model.task;

import org.example.jpa.domain.shard.BaseEntity;
import org.example.jpa.domain.shard.DomainEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author renc
 */
@Table(
        name = "T_RE_TASK",
        uniqueConstraints = @UniqueConstraint(
                name = "UDX_T_RE_TASK_NAME_CREATOR",
                columnNames = {"NAME", "CREATOR"}
        )
)
@Entity
public class Task extends BaseEntity implements DomainEntity<Task> {

    /**
     * 任务标识
     */
    @Embedded
    private TaskId taskId;

    /**
     * 任务关联数据集标识
     */
    @Embedded
    private DatasetId datasetId;

    /**
     * 任务优先级
     */
    @Column(name = "PRIORITY")
    private int priority;

    /**
     * 任务状态
     */
    @Enumerated
    @Column(name = "STATE")
    private TaskState taskState;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Decision> decisions;

    /**
     * 任务运行周期
     */
    @Embedded
    private TaskRuntime taskRuntime;

    /**
     * 任务锁定者
     */
    @Embedded
    private Locker locker;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT")
    @CreationTimestamp
    private LocalDateTime createdAt;

    protected Task() {
    }

    public Task(TaskId taskId, DatasetId datasetId) {
        Assert.notNull(taskId, "任务唯一标识不能为空");
        Assert.notNull(datasetId, "任务关联数据集不能为空");
        this.taskId = taskId;
        this.datasetId = datasetId;
        this.priority = 5;
        this.taskState = TaskState.LOADING;
        this.taskRuntime = TaskRuntime.emptyTaskRuntime();
        this.locker = Locker.emptyLocker();
    }

    public TaskId taskId() {
        return taskId;
    }

    public void setDecisions(List<Decision> decisions) {
        this.decisions = decisions;
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
}
