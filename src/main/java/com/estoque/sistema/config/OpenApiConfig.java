package com.estoque.sistema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoreEstoque API")
                        .version("1.0")
                        .description("API do sistema CoreEstoque com auditoria, controle de validade e alertas automáticos.")
                        .contact(new Contact().name("Suporte Técnico").email("suporte@restaurante.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
