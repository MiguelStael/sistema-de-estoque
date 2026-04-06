package com.estoque.sistema.controller;

import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.repository.IngredienteRepository;
import com.estoque.sistema.service.MovimentacaoService;
import com.estoque.sistema.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping("/estoque/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Movimentações de Estoque", description = "Monitoramento e registro manual de perdas e ajustes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final IngredienteRepository ingredienteRepository;

    @PostMapping("/perda")
    @Operation(summary = "Registra uma perda manual de ingrediente")
    public ResponseEntity<Void> registrarPerda(@RequestParam @NonNull Long ingredienteId, @RequestParam @NonNull BigDecimal quantidade, @RequestParam @NonNull String motivo) {
        Ingrediente ingrediente = ingredienteRepository.findById(Objects.requireNonNull(ingredienteId))
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado."));
        
        ingrediente.setQuantidade(ingrediente.getQuantidade().subtract(quantidade));
        ingredienteRepository.save(ingrediente);
        
        movimentacaoService.registrarPerda(ingrediente, quantidade, motivo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ajuste")
    @Operation(summary = "Registra um ajuste de inventário")
    public ResponseEntity<Void> registrarAjuste(@RequestParam @NonNull Long ingredienteId, @RequestParam @NonNull BigDecimal quantidadeDiferenca, @RequestParam @NonNull String motivo) {
        Ingrediente ingrediente = ingredienteRepository.findById(Objects.requireNonNull(ingredienteId))
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao encontrado."));
        
        ingrediente.setQuantidade(ingrediente.getQuantidade().add(quantidadeDiferenca));
        ingredienteRepository.save(ingrediente);
        
        movimentacaoService.registrarAjuste(ingrediente, quantidadeDiferenca, motivo);
        return ResponseEntity.ok().build();
    }
}
