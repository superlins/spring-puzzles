package org.example.r2dbc.conf;

import io.r2dbc.spi.ConnectionFactory;
import org.example.r2dbc.conf.converter.R2dbcConverterCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author renc
 * @see AbstractR2dbcConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class R2dbcCustomConversionsConfig {

    private final R2dbcDialect dialect;

    public R2dbcCustomConversionsConfig(ConnectionFactory connectionFactory) {
        this.dialect = DialectResolver.getDialect(connectionFactory);;
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(List<R2dbcConverterCustomizer> r2dbcConverterCustomizers) {
        List<Object> converters = new ArrayList<>(this.dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        return new R2dbcCustomConversions(
                CustomConversions.StoreConversions.of(this.dialect.getSimpleTypeHolder(), converters),
                r2dbcConverterCustomizers);
    }
}
