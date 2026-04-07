package com.estoque.sistema.service;

import com.estoque.sistema.dto.AlertaEstoqueDTO;
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
public class MonitoramentoEstoqueService {

    private final IngredienteRepository ingredienteRepository;
    private final ProdutoRepository produtoRepository;

    private static final double LIMITE_CRITICO = 30.0;
    private static final double LIMITE_ALERTA = 60.0;

    public List<AlertaEstoqueDTO> listarAlertasCriticos() {
        List<AlertaEstoqueDTO> alertas = new ArrayList<>();

        alertas.addAll(ingredienteRepository.findAllCriticos().stream()
                .map(this::mapIngredienteToAlerta)
                .collect(Collectors.toList()));

        alertas.addAll(produtoRepository.findAllCriticos().stream()
                .map(this::mapProdutoToAlerta)
                .collect(Collectors.toList()));

        return alertas.stream()
                .filter(a -> !"NORMAL".equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    private AlertaEstoqueDTO mapIngredienteToAlerta(Ingrediente i) {
        BigDecimal qtdAtual = i.getQuantidade() != null ? i.getQuantidade() : BigDecimal.ZERO;
        BigDecimal qtdMinima = i.getQuantidadeMinima() != null ? i.getQuantidadeMinima() : BigDecimal.ONE;
        
        double percentual = calcularPercentual(qtdAtual, qtdMinima);

        return AlertaEstoqueDTO.builder()
                .nome(i.getNome())
                .tipo("INGREDIENTE")
                .quantidadeAtual(qtdAtual)
                .quantidadeMinima(qtdMinima)
                .unidadeMedida(i.getUnidadeMedida().name())
                .percentualRestante(percentual)
                .status(definirStatus(percentual))
                .build();
    }

    private AlertaEstoqueDTO mapProdutoToAlerta(Produto p) {
        BigDecimal qtdAtual = new BigDecimal(p.getQuantidade() != null ? p.getQuantidade() : 0);
        BigDecimal qtdMinima = new BigDecimal(p.getQuantidadeMinima() != null ? p.getQuantidadeMinima() : 1);
        
        double percentual = calcularPercentual(qtdAtual, qtdMinima);

        return AlertaEstoqueDTO.builder()
                .nome(p.getNome())
                .tipo("PRODUTO")
                .quantidadeAtual(qtdAtual)
                .quantidadeMinima(qtdMinima)
                .unidadeMedida("UNIDADE")
                .percentualRestante(percentual)
                .status(definirStatus(percentual))
                .build();
    }

    private double calcularPercentual(BigDecimal atual, BigDecimal minima) {
        if (minima.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return atual.divide(minima, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue();
    }

    private String definirStatus(double percentual) {
        if (percentual <= LIMITE_CRITICO) return "CRITICO";
        if (percentual <= LIMITE_ALERTA) return "ALERTA";
        return "NORMAL";
    }
}
