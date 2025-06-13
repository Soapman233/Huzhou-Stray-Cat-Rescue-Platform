package com.hzsca.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置 LocalDateTime 序列化格式
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}