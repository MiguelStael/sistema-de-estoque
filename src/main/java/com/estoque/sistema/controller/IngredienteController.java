package com.estoque.sistema.controller;

import com.estoque.sistema.dto.IngredienteRequestDTO;
import com.estoque.sistema.dto.IngredienteResponseDTO;
import com.estoque.sistema.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estoque/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<Page<IngredienteResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") @NonNull Pageable pageable) {
        return ResponseEntity.ok(ingredienteService.listarTodos(pageable));
    }

    @GetMapping("/criticos")
    public ResponseEntity<List<IngredienteResponseDTO>> listarCriticos() {
        return ResponseEntity.ok(ingredienteService.listarCriticos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponseDTO> buscarPorId(@PathVariable @NonNull Long id) {
        return ingredienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IngredienteResponseDTO> criar(@RequestBody @Valid @NonNull IngredienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredienteService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteResponseDTO> atualizar(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid @NonNull IngredienteRequestDTO dto) {
        return ResponseEntity.ok(ingredienteService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/entrada")
    public ResponseEntity<IngredienteResponseDTO> adicionarLote(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal quantidadeReq = body.getOrDefault("quantidade", BigDecimal.ZERO);
        BigDecimal quantidade = quantidadeReq != null ? quantidadeReq : BigDecimal.ZERO;

        return ResponseEntity.ok(ingredienteService.adicionarLote(id, quantidade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @NonNull Long id) {
        ingredienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
