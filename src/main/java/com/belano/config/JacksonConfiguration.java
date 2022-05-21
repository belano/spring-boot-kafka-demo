package com.belano.config;

import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL;

import java.util.TimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfiguration {
    @Bean
    @Primary
    public ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .timeZone(TimeZone.getDefault())
                .defaultViewInclusion(true)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .featuresToEnable(READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        SerializationFeature.FAIL_ON_EMPTY_BEANS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
