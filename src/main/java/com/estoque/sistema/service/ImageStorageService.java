package com.estoque.sistema.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final int IMAGE_WIDTH  = 800;
    private static final int IMAGE_HEIGHT = 800;

    private final Path fileStorageLocation;

    public ImageStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório de uploads.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFileName.contains("..")) {
            throw new RuntimeException("Caminho inválido: " + originalFileName);
        }

        String newFileName = UUID.randomUUID() + ".jpg";
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);

        try {
            Thumbnails.of(file.getInputStream())
                    .size(IMAGE_WIDTH, IMAGE_HEIGHT)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .outputQuality(0.85)
                    .toFile(targetLocation.toFile());
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível processar a imagem: " + originalFileName, ex);
        }

        return newFileName;
    }
}
