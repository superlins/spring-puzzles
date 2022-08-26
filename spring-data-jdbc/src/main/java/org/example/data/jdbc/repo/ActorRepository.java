package org.example.data.jdbc.repo;

import org.example.data.jdbc.domain.Actor;
import org.springframework.data.repository.CrudRepository;

/**
 * @author renc
 */
public interface ActorRepository extends CrudRepository<Actor, Integer> {
}
