package com.estoque.sistema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadDirectoryHealthIndicator implements HealthIndicator {

    private final String uploadDir;

    public UploadDirectoryHealthIndicator(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public Health health() {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            File directory = path.toFile();

            if (!directory.exists()) {
                return Health.down()
                        .withDetail("erro", "Diretório de uploads não existe")
                        .withDetail("caminho", path.toString())
                        .build();
            }

            if (!directory.isDirectory()) {
                return Health.down()
                        .withDetail("erro", "O caminho especificado não é um diretório")
                        .withDetail("caminho", path.toString())
                        .build();
            }

            if (!directory.canWrite()) {
                return Health.down()
                        .withDetail("erro", "Sem permissão de escrita no diretório de uploads")
                        .withDetail("caminho", path.toString())
                        .build();
            }

            return Health.up()
                    .withDetail("caminho", path.toString())
                    .withDetail("permissao_escrita", true)
                    .build();

        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
