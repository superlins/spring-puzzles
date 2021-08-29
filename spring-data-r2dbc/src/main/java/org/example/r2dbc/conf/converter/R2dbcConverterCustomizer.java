package org.example.r2dbc.conf.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.EnumWriteSupport;

/**
 * @author renc
 * @see EnumWriteSupport
 */
public interface R2dbcConverterCustomizer<S, T> extends Converter<S, T> {
}
