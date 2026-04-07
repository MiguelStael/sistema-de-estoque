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

        // Validação de tipo de arquivo
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
                                    !contentType.equals("image/png") && 
                                    !contentType.equals("image/webp"))) {
            throw new RuntimeException("Formato de imagem inválido. Apenas JPEG, PNG e WebP são aceitos.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("Nome do arquivo inválido.");
        }
        originalFileName = StringUtils.cleanPath(originalFileName);

        if (originalFileName.contains("..")) {
            throw new RuntimeException("Caminho inválido: " + originalFileName);
        }

        String fileNameWithoutExtension = UUID.randomUUID().toString();
        String newFileName = fileNameWithoutExtension + ".webp";
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);

        try {
            Thumbnails.of(file.getInputStream())
                    .width(IMAGE_WIDTH)
                    .keepAspectRatio(true)
                    .outputFormat("webp")
                    .outputQuality(0.80)
                    .toFile(targetLocation.toFile());
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível processar a imagem: " + originalFileName, ex);
        }

        return newFileName;
    }

    public void deleteFile(String urlImagem) {
        if (urlImagem == null || urlImagem.isBlank()) {
            return;
        }

        try {
            // Extrair o nome do arquivo da URL (ex: http://.../imagens/uuid.webp)
            String fileName = urlImagem.substring(urlImagem.lastIndexOf("/") + 1);
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException ex) {
            // Logar erro, mas permitir que o fluxo continue
            System.err.println("Erro ao deletar arquivo: " + urlImagem);
        }
    }
}
