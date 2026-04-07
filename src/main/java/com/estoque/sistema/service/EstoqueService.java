package com.estoque.sistema.service;

import com.estoque.sistema.dto.EstoqueAlertaDTO;
import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.model.Produto;
import com.estoque.sistema.repository.IngredienteRepository;
import com.estoque.sistema.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final IngredienteRepository ingredienteRepository;
    private final ProdutoRepository produtoRepository;

    public List<EstoqueAlertaDTO> listarAlertasCriticos() {
        List<EstoqueAlertaDTO> alertas = new ArrayList<>();

        // Buscar ingredientes críticos
        alertas.addAll(ingredienteRepository.findAllCriticos().stream()
                .map(this::mapIngredienteToAlerta)
                .collect(Collectors.toList()));

        // Buscar produtos críticos
        alertas.addAll(produtoRepository.findAllCriticos().stream()
                .map(this::mapProdutoToAlerta)
                .collect(Collectors.toList()));

        return alertas;
    }

    private EstoqueAlertaDTO mapIngredienteToAlerta(Ingrediente i) {
        BigDecimal qtdAtual = i.getQuantidade() != null ? i.getQuantidade() : BigDecimal.ZERO;
        BigDecimal qtdMinima = i.getQuantidadeMinima() != null ? i.getQuantidadeMinima() : BigDecimal.ONE;
        
        double percentual = qtdMinima.compareTo(BigDecimal.ZERO) > 0 
            ? qtdAtual.divide(qtdMinima, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue() 
            : 0;

        return EstoqueAlertaDTO.builder()
                .nome(i.getNome())
                .tipo("INGREDIENTE")
                .quantidadeAtual(qtdAtual)
                .quantidadeMinima(qtdMinima)
                .unidadeMedida(i.getUnidadeMedida().name())
                .percentualRestante(percentual)
                .status(percentual <= 50 ? "CRITICO" : "ALERTA")
                .build();
    }

    private EstoqueAlertaDTO mapProdutoToAlerta(Produto p) {
        BigDecimal qtdAtual = new BigDecimal(p.getQuantidade() != null ? p.getQuantidade() : 0);
        BigDecimal qtdMinima = new BigDecimal(p.getQuantidadeMinima() != null ? p.getQuantidadeMinima() : 1);
        
        double percentual = qtdMinima.compareTo(BigDecimal.ZERO) > 0 
            ? qtdAtual.divide(qtdMinima, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue() 
            : 0;

        return EstoqueAlertaDTO.builder()
                .nome(p.getNome())
                .tipo("PRODUTO")
                .quantidadeAtual(qtdAtual)
                .quantidadeMinima(qtdMinima)
                .unidadeMedida("UNIDADE")
                .percentualRestante(percentual)
                .status(percentual <= 30 ? "CRITICO" : "ALERTA")
                .build();
    }
}
