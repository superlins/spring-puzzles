package org.example.jpa.domain.shard;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

/**
 * @author renc
 */
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long id() {
        return id;
    }
}