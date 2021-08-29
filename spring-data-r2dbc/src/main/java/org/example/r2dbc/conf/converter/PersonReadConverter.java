package org.example.r2dbc.conf.converter;

import io.r2dbc.spi.Row;
import org.example.r2dbc.domain.Person;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class PersonReadConverter implements R2dbcConverterCustomizer<Row, Person> {

    public Person convert(Row source) {
        Person p = new Person(
                source.get("id", String.class),
                source.get("name", String.class),
                source.get("age", Integer.class));
        return p;
    }
}