package org.example.r2dbc.repo;

import org.example.r2dbc.domain.Person;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, String> {

    @Modifying
    @Query("UPDATE person SET name = :name where id = :id")
    Mono<Integer> setFixedFirstnameByNameParam(@Param("id") String id, @Param("name") String name);

    @Modifying
    @Query("UPDATE person SET name = :name where id = :id")
    Mono<Integer> setFixedFirstnameByIndexParam(String name, String id);

    @Modifying
    @Query("UPDATE person SET name = :#{[1]} where id = :#{[0]}")
    Mono<Integer> setFixedFirstnameBySpEL(String name, String id);
}