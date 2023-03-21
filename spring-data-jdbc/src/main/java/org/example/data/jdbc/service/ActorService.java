package org.example.data.jdbc.service;

import org.example.data.jdbc.domain.Actor;
import org.springframework.data.domain.Pageable;

/**
 * @author renc
 */
public interface ActorService {

    Iterable<Actor> actors(Actor actor, Pageable pageable);
}
