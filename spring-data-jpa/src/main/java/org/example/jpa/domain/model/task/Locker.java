package org.example.jpa.domain.model.task;

import org.example.jpa.domain.shard.ValueObject;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author renc
 */
@Embeddable
public class Locker implements ValueObject<Locker> {

    private static final Locker EMPTY = new Locker();

    /**
     * 任务锁定人
     */
    @Column(name = "LOCKER")
    private String name;

    protected Locker() {
    }

    private Locker(String name) {
        Assert.hasText(name, "锁定人不能为空");
        this.name = name;
    }

    public static Locker emptyLocker() {
        return EMPTY;
    }

    static Locker lockedFor(String name) {
        return new Locker(name);
    }

    boolean isLocked() {
        return this != EMPTY;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Locker locker = (Locker) o;

        return sameValueAs(locker);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean sameValueAs(Locker other) {
        return other != null && this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
