package com.estoque.sistema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI sistemaEstoqueOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API do Sistema de Estoque")
                        .description("Backend de gerenciamento de estoque.")
                        .version("v0.0.1"));
    }
}