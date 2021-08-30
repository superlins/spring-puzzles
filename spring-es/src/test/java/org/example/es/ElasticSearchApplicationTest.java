package org.example.es;

import org.example.es.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author renc
 */
@SpringBootTest
class ElasticSearchApplicationTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    @Test
    void init() {
        assertNotNull(elasticsearchRestTemplate);
        assertNotNull(reactiveElasticsearchTemplate);
    }

    @Test
    void createIndex() {
        reactiveElasticsearchTemplate.indexOps(Person.class).create();
    }
}