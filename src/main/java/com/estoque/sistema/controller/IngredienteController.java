package com.estoque.sistema.controller;

import com.estoque.sistema.dto.IngredienteRequestDTO;
import com.estoque.sistema.dto.IngredienteResponseDTO;
import com.estoque.sistema.service.IngredienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Estoque: Ingredientes", description = "Gerenciamento de insumos e matérias-primas")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os ingredientes", description = "Retorna uma lista paginada de todos os insumos ativos no sistema.")
    public ResponseEntity<Page<IngredienteResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") @NonNull Pageable pageable) {
        return ResponseEntity.ok(ingredienteService.listarTodos(pageable));
    }

    @GetMapping("/criticos")
    @Operation(summary = "Listar ingredientes críticos", description = "Retorna apenas os itens que estão abaixo da quantidade mínima permitida.")
    public ResponseEntity<List<IngredienteResponseDTO>> listarCriticos() {
        return ResponseEntity.ok(ingredienteService.listarCriticos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ingrediente por ID")
    public ResponseEntity<IngredienteResponseDTO> buscarPorId(@PathVariable @NonNull Long id) {
        return ingredienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo ingrediente", description = "Cadastra um insumo com nome, unidade de medida, custo e data de validade.")
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
    @Operation(summary = "Registrar entrada de estoque", description = "Aumenta a quantidade de um ingrediente (ex: após uma compra).")
    public ResponseEntity<IngredienteResponseDTO> adicionarLote(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal quantidadeReq = body.getOrDefault("quantidade", BigDecimal.ZERO);
        BigDecimal quantidade = quantidadeReq != null ? quantidadeReq : BigDecimal.ZERO;

        return ResponseEntity.ok(ingredienteService.adicionarLote(id, java.util.Objects.requireNonNull(quantidade)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluír ingrediente (Soft Delete)", description = "Marca o ingrediente como inativo, mantendo-o no banco para integridade histórica.")
    public ResponseEntity<Void> deletar(@PathVariable @NonNull Long id) {
        ingredienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
