package com.estoque.sistema.controller;

import com.estoque.sistema.dto.EmailRequestDTO;
import com.estoque.sistema.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> enviarEmail(@Valid @RequestBody EmailRequestDTO dto) {
        emailService.enviarEmailPersonalizado(dto);
        return ResponseEntity.ok(Map.of("mensagem", "Processo de envio de e-mail iniciado para: " + dto.getPara()));
    }
}
