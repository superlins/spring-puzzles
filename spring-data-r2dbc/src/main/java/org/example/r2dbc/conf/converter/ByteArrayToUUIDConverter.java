package org.example.r2dbc.conf.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.UUID;

@ReadingConverter
public class ByteArrayToUUIDConverter implements Converter<byte[], UUID> {
    @Override
    public UUID convert(byte[] source) {
        return UUID.nameUUIDFromBytes(source);
    }
}