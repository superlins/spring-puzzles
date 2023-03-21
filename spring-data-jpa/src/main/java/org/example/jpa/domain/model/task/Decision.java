package org.example.jpa.domain.model.task;

import org.example.jpa.domain.shard.BaseEntity;
import org.example.jpa.domain.shard.DomainEntity;

import javax.persistence.*;

/**
 * @author renc
 */
@Table(name = "T_RE_DECISION")
@Entity
public class Decision extends BaseEntity implements DomainEntity<Decision> {

    @Column(name = "NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    protected Decision() {
    }

    public Decision(Task task, String name) {
        this.task = task;
        this.name = name;
    }

    @Override
    public boolean sameIdentityAs(Decision other) {
        return false;
    }
}
