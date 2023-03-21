package org.example.jpa.infrastructure.conf.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author renc
 */
@Configuration(proxyBeanMethods = false)
public class JacksonSupports {

    private static final String DEFAULT_TIME_ZONE = "GMT+8";

    private static final String DEFAULT_LOCAL_DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.timeZone(DEFAULT_TIME_ZONE)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                // .featuresToEnable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .serializers(new LocalDateTimeSerializer(ofPattern(DEFAULT_LOCAL_DATETIME_FORMATTER)))
                .deserializers(new LocalDateTimeDeserializer(ofPattern(DEFAULT_LOCAL_DATETIME_FORMATTER)))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                // .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE) // NON-NECESSARY
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                ;
    }
}
