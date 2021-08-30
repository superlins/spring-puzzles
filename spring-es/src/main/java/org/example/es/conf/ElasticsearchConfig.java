package org.example.es.conf;

import org.example.es.conf.converter.AddressToMap;
import org.example.es.conf.converter.MapToAddress;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.mapping.model.FieldNamingStrategy;

import java.util.Arrays;

/**
 * @author renc
 * @see ReactiveElasticsearchRestClientAutoConfiguration &ElasticsearchDataConfiguration. class
 */
@Configuration(proxyBeanMethods = false)
public class ElasticsearchConfig extends ElasticsearchConfigurationSupport {

    @Override
    protected FieldNamingStrategy fieldNamingStrategy() {
        return super.fieldNamingStrategy();
    }

    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
                Arrays.asList(new AddressToMap(), new MapToAddress()));
    }
}
