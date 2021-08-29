package org.example.r2dbc.conf.converter;

import org.example.r2dbc.domain.Person;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class PersonWriteConverter implements R2dbcConverterCustomizer<Person, OutboundRow> {

    public OutboundRow convert(Person source) {
        OutboundRow row = new OutboundRow();
        row.put("id", Parameter.from(source.getId()));
        row.put("name", Parameter.from(source.getName()));
        row.put("age", Parameter.from(source.getAge()));
        return row;
    }
}