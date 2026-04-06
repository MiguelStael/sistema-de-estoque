package com.estoque.sistema.controller;

import com.estoque.sistema.dto.MesaRequestDTO;
import com.estoque.sistema.dto.MesaResponseDTO;
import com.estoque.sistema.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Gestão de mesas do restaurante")
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @Operation(summary = "Listar todas as mesas ativas")
    public List<MesaResponseDTO> listarTodas() {
        return mesaService.listarTodas();
    }

    @PostMapping
    @Operation(summary = "Criar uma nova mesa")
    public ResponseEntity<MesaResponseDTO> criar(@RequestBody @Valid MesaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar detalhes de uma mesa")
    public MesaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid MesaRequestDTO dto) {
        return mesaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Arquivar uma mesa (soft-delete)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        mesaService.deletar(id);
    }
}
