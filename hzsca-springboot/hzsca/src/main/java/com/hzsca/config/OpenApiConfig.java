package com.hzsca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HZSCA API 文档")
                        .version("1.0")
                        .description("流浪猫救助 API 接口文档")
                        .contact(new Contact()
                                .name("HZSCA Team")
                                .email("support@hzsca.com")));
    }
}