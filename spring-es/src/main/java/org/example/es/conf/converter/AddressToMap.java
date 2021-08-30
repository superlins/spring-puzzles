package org.example.es.conf.converter;

import org.example.es.domain.Address;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.LinkedHashMap;
import java.util.Map;

@WritingConverter
public
class AddressToMap implements Converter<Address, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Address source) {

        LinkedHashMap<String, Object> target = new LinkedHashMap<>();
        target.put("ciudad", source.getCity());
        // ...

        return target;
    }
}
