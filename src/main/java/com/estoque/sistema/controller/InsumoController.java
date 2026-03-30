package com.estoque.sistema.controller;

import com.estoque.sistema.dto.InsumoRequestDTO;
import com.estoque.sistema.dto.InsumoResponseDTO;
import com.estoque.sistema.service.InsumoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/estoque/insumos")
@Tag(name = "Insumos", description = "Controlo de stock de ingredientes e matérias-primas")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @Operation(summary = "Listar insumos (paginado)")
    @GetMapping
    public ResponseEntity<Page<InsumoResponseDTO>> listar(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "nome") @NonNull Pageable pageable) {
        return ResponseEntity.ok(insumoService.listarTodos(pageable));
    }

    @Operation(summary = "Listar insumos críticos")
    @GetMapping("/criticos")
    public ResponseEntity<List<InsumoResponseDTO>> listarCriticos() {
        return ResponseEntity.ok(insumoService.listarCriticos());
    }

    @Operation(summary = "Buscar insumo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> buscarPorId(@PathVariable @NonNull Long id) {
        return insumoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar insumo")
    @PostMapping
    public ResponseEntity<InsumoResponseDTO> criar(@RequestBody @Valid @NonNull InsumoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(insumoService.criar(dto));
    }

    @Operation(summary = "Atualizar insumo")
    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> atualizar(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid @NonNull InsumoRequestDTO dto) {
        return ResponseEntity.ok(insumoService.atualizar(id, dto));
    }

    @Operation(summary = "Entrada de lote")
    @PatchMapping("/{id}/entrada")
    public ResponseEntity<InsumoResponseDTO> adicionarLote(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal quantidade = body.getOrDefault("quantidade", BigDecimal.ZERO);
        if (quantidade == null)
            quantidade = BigDecimal.ZERO;

        return ResponseEntity.ok(insumoService.adicionarLote(id, quantidade));
    }

    @Operation(summary = "Remover insumo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @NonNull Long id) {
        insumoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
