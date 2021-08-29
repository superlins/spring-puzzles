package org.example.r2dbc.conf.converter;

import org.example.r2dbc.constant.Type;
import org.springframework.stereotype.Component;

@Component
public class TypeWriteConverter implements R2dbcConverterCustomizer<Integer, Type> {

    @Override
    public Type convert(Integer ordinal) {
        Type[] values = Type.values();
        for (Type type : values) {
            if (ordinal == type.ordinal()) {
                return type;
            }
        }
        return Type.NEW;
    }
}