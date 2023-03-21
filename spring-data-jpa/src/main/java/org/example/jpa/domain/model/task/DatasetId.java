package org.example.jpa.domain.model.task;

import org.example.jpa.domain.shard.ValueObject;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author renc
 */
@Embeddable
public class DatasetId implements ValueObject<DatasetId> {

    @Column(name = "DATASET")
    private String id;

    protected DatasetId() {
    }

    public DatasetId(String id) {
        Assert.hasText(id, "数据集标识不能为空");
        this.id = id;
    }

    public String idString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasetId other = (DatasetId) o;

        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean sameValueAs(DatasetId other) {
        return other != null && this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return id;
    }
}
