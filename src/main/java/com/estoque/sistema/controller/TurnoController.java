package com.estoque.sistema.controller;

import com.estoque.sistema.dto.TurnoResponseDTO;
import com.estoque.sistema.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turno")
@Tag(name = "Turno", description = "Gerenciamento do expediente do restaurante")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping("/abrir")
    @Operation(summary = "Abre o turno do expediente")
    public ResponseEntity<TurnoResponseDTO> abrirTurno(@RequestParam String responsavel) {
        return ResponseEntity.ok(turnoService.abrirTurno(responsavel));
    }

    @PostMapping("/fechar")
    @Operation(summary = "Fecha o turno do expediente e calcula a duração")
    public ResponseEntity<TurnoResponseDTO> fecharTurno() {
        return ResponseEntity.ok(turnoService.fecharTurno());
    }

    @GetMapping("/atual")
    @Operation(summary = "Retorna o turno atualmente em aberto")
    public ResponseEntity<TurnoResponseDTO> turnoAtual() {
        return ResponseEntity.ok(turnoService.buscarTurnoAtual());
    }
}
