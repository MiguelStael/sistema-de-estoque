package com.estoque.sistema.controller;

import com.estoque.sistema.dto.TurnoResponseDTO;
import com.estoque.sistema.service.TurnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turno")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<TurnoResponseDTO> abrirTurno(@RequestParam String responsavel) {
        return ResponseEntity.ok(turnoService.abrirTurno(responsavel));
    }

    @PostMapping("/fechar")
    public ResponseEntity<TurnoResponseDTO> fecharTurno() {
        return ResponseEntity.ok(turnoService.fecharTurno());
    }

    @GetMapping("/atual")
    public ResponseEntity<TurnoResponseDTO> turnoAtual() {
        return ResponseEntity.ok(turnoService.buscarTurnoAtual());
    }
}
