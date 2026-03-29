package com.estoque.sistema.controller;

import com.estoque.sistema.model.Insumo;
import com.estoque.sistema.service.InsumoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estoque/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    public ResponseEntity<List<Insumo>> listar() {
        return ResponseEntity.ok(insumoService.listarTodos());
    }

    @GetMapping("/criticos")
    public ResponseEntity<List<Insumo>> listarCriticos() {
        return ResponseEntity.ok(insumoService.listarCriticos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insumo> buscarPorId(@PathVariable @org.springframework.lang.NonNull Long id) {
        return insumoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Insumo> criar(@RequestBody @org.springframework.lang.NonNull Insumo insumo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.criar(insumo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insumo> atualizar(@PathVariable @org.springframework.lang.NonNull Long id, @RequestBody @org.springframework.lang.NonNull Insumo dados) {
        try {
            return ResponseEntity.ok(insumoService.atualizar(id, dados));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/entrada")
    public ResponseEntity<Insumo> adicionarLote(
            @PathVariable @org.springframework.lang.NonNull Long id,
            @RequestBody Map<String, BigDecimal> body
    ) {
        BigDecimal quantidade = body.getOrDefault("quantidade", BigDecimal.ZERO);
        if (quantidade == null) quantidade = BigDecimal.ZERO;
        try {
            return ResponseEntity.ok(insumoService.adicionarLote(id, quantidade));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @org.springframework.lang.NonNull Long id) {
        insumoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
