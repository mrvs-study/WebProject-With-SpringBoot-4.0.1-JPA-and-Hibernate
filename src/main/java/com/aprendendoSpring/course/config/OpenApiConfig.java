package com.aprendendoSpring.course.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI easyMarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Easy Market API")
                        .description("API REST para gerenciamento de mercadinho/PDV.")
                        .version("1.0.0"));
    }
}
