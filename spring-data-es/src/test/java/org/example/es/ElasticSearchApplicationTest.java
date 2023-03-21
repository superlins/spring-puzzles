package org.example.es;

import org.elasticsearch.index.query.QueryBuilders;
import org.example.es.conf.repo.PersonReactiveRepository;
import org.example.es.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.*;
import reactor.core.publisher.Flux;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * @author renc
 */
@SpringBootTest
class ElasticSearchApplicationTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    @Autowired
    private PersonReactiveRepository repository;

    @Test
    void init() {
        assertNotNull(elasticsearchRestTemplate);
        assertNotNull(reactiveElasticsearchTemplate);
    }

    @Test
    void createIndex() {
        reactiveElasticsearchTemplate.indexOps(Person.class).create();
    }

    @Test
    void searchForCriteriaQuery() {
        Criteria criteria = Criteria.where("price").is(42.0).and("firstname").is("James")  ;
        Criteria miller = new Criteria("lastName").is("Miller")
                .subCriteria(
                        new Criteria().or("firstName").is("John")
                                .or("firstName").is("Jack")
                );
        Query query = new CriteriaQuery(criteria);
    }

    @Test
    void searchForStringQuery() {
        Query query = new StringQuery("{ \"match\": { \"firstname\": { \"query\": \"Jack\" } } } ");
        Flux<SearchHit<Person>> search = reactiveElasticsearchTemplate.search(query, Person.class);
    }

    @Test
    void searchForNativeQuery() {
        Query query = new NativeSearchQueryBuilder()
                .addAggregation(terms("lastnames").field("lastname").size(10)) //
                .withQuery(QueryBuilders.matchQuery("firstname", "xxx"))
                .build();

        Flux<SearchHit<Person>> search = reactiveElasticsearchTemplate.search(query, Person.class);
    }



    @Test
    public void sortsElementsCorrectly() {
        Flux<Person> persons = repository.findAll(Sort.by(new Sort.Order(ASC, "lastname")));

        // ...
    }
}