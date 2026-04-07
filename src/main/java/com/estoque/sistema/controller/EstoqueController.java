package com.estoque.sistema.controller;

import com.estoque.sistema.dto.EstoqueAlertaDTO;
import com.estoque.sistema.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoque/dashboard")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Endpoints de inteligência e monitoramento de estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Operation(summary = "Listar alertas de estoque crítico", description = "Retorna ingredientes e produtos que estão com estoque abaixo do mínimo configurado.")
    @GetMapping("/alertas-criticos")
    public ResponseEntity<List<EstoqueAlertaDTO>> listarAlertasCriticos() {
        return ResponseEntity.ok(estoqueService.listarAlertasCriticos());
    }
}
