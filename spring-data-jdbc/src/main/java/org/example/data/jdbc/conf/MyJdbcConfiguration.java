package org.example.data.jdbc.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.jdbc.domain.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;

@Configuration(proxyBeanMethods = false)
class MyJdbcConfiguration extends AbstractJdbcConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(new DetailToStringConverter(), new StringToDetailConverter()));
    }

    @WritingConverter
    public class DetailToStringConverter implements Converter<Actor.Detail, String> {

        @Override
        public String convert(Actor.Detail source) {
            try {
                return objectMapper.writeValueAsString(source);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @ReadingConverter
    public class StringToDetailConverter implements Converter<String, Actor.Detail> {

        @Override
        public Actor.Detail convert(String source) {
            try {
                return objectMapper.readValue(source, Actor.Detail.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}