package org.example.es.conf.repo;

import org.example.es.domain.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PersonRepository extends Repository<Person, String> {

    List<Person> findByNameAndPrice(String name, Integer price);

    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    Page<Person> findByName(String name, Pageable pageable);
}