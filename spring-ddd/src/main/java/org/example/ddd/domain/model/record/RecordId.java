package org.example.ddd.domain.model.record;

import org.example.ddd.domain.shard.ValueObject;

/**
 * @author renc
 */
public class RecordId implements ValueObject<RecordId> {

    private String id;

    public RecordId(String id) {
        this.id = id;
    }

    @Override
    public boolean sameValueAs(RecordId other) {
        return false;
    }

    RecordId() {}
}
