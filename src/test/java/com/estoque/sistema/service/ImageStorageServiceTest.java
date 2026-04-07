package com.estoque.sistema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        imageStorageService = new ImageStorageService(tempDir.toString());
    }

    @Test
    void shouldStoreValidImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "imagem", "test.jpg", "image/jpeg", "image content".getBytes());

        String fileName = imageStorageService.storeFile(file);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".webp"));
        assertTrue(Files.exists(tempDir.resolve(fileName)));
    }

    @Test
    void shouldThrowExceptionForInvalidType() {
        MockMultipartFile file = new MockMultipartFile(
                "documento", "test.txt", "text/plain", "text content".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageStorageService.storeFile(file);
        });

        assertEquals("Formato de imagem inválido. Apenas JPEG, PNG e WebP são aceitos.", exception.getMessage());
    }

    @Test
    void shouldDeleteFile() throws IOException {
        Path file = tempDir.resolve("to-delete.webp");
        Files.createFile(file);
        assertTrue(Files.exists(file));

        String url = "http://localhost:8080/imagens/to-delete.webp";
        imageStorageService.deleteFile(url);

        assertFalse(Files.exists(file));
    }
}
