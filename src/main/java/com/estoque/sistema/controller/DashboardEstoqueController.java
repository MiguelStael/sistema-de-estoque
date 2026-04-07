package com.estoque.sistema.controller;

import com.estoque.sistema.config.AgendadorAlertasEstoque;
import com.estoque.sistema.dto.AlertaEstoqueDTO;
import com.estoque.sistema.service.MonitoramentoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estoque/dashboard")
@RequiredArgsConstructor
public class DashboardEstoqueController {

    private final MonitoramentoEstoqueService monitoramentoEstoqueService;
    private final AgendadorAlertasEstoque agendadorAlertasEstoque;

    @GetMapping("/alertas-criticos")
    public ResponseEntity<List<AlertaEstoqueDTO>> listarAlertasCriticos() {
        return ResponseEntity.ok(monitoramentoEstoqueService.listarAlertasCriticos());
    }

    @PostMapping("/alertas-criticos/notificar")
    public ResponseEntity<String> dispararAlertasEmail() {
        agendadorAlertasEstoque.processarAlertas();
        return ResponseEntity.ok("Processamento de alertas de e-mail disparado.");
    }
}
