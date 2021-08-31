package org.example.es.controller;

import org.example.es.domain.Person;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class TestController {

  private ElasticsearchRestTemplate elasticsearchRestTemplate;

  public TestController(ElasticsearchRestTemplate elasticsearchRestTemplate) {
    this.elasticsearchRestTemplate = elasticsearchRestTemplate;
  }

  @PostMapping("/person")
  public String save(@RequestBody Person person) {

    IndexQuery indexQuery = new IndexQueryBuilder()
      .withId(person.getId())
      .withObject(person)
      .build();
    String documentId = elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of("person"));
    return documentId;
  }

  @GetMapping("/person/{id}")
  public Person findById(@PathVariable("id")  String id) {
    Person person = elasticsearchRestTemplate
      .get(id, Person.class);
    return person;
  }
}