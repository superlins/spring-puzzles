package org.example.jpa.domain.shard;

/**
 * A domain entity, as explained in the DDD book.
 *
 * @author renc
 */
public interface DomainEntity<T> {

    /**
     * Entities compare by identity, not by attributes.
     *
     * @param other The other entity.
     * @return true if the identities are the same, regardless of other attributes.
     */
    boolean sameIdentityAs(T other);
}
