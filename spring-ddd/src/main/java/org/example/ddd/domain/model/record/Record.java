package org.example.ddd.domain.model.record;

import org.example.ddd.domain.shard.Entity;

/**
 * @author renc
 */
public class Record implements Entity<Record> {

    private RecordId recordId;

    private String content;

    private String sender;

    private String receiver;

    private Long timestamp;

    private String labels;

    public Record(RecordId recordId, String content, String sender, String receiver, Long timestamp) {
        this.recordId = recordId;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    @Override
    public boolean sameIdentityAs(Record other) {
        return false;
    }

    Record() {}

    private Long id;
}
