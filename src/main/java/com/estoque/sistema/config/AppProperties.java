package com.estoque.sistema.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Upload upload = new Upload();
    private final Notificacao notificacao = new Notificacao();
    private final Cors cors = new Cors();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMs;
    }

    @Getter
    @Setter
    public static class Upload {
        private String dir;
    }

    @Getter
    @Setter
    public static class Notificacao {
        private final Email email = new Email();

        @Getter
        @Setter
        public static class Email {
            private String destino;
        }
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOriginPatterns = Collections.emptyList();
    }
}
