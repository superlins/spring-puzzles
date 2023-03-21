package org.example.es.conf.converter;

import org.example.es.domain.Address;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
public
class MapToAddress implements Converter<Map<String, Object>, Address> {

    @Override
    public Address convert(Map<String, Object> source) {
        Address address = new Address();

        // ...
        return address;
    }
}