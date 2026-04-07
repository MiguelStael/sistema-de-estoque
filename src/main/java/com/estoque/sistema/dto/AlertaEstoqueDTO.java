package com.estoque.sistema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaEstoqueDTO {

    private String nome;
    private String tipo; // "PRODUTO" ou "INGREDIENTE"
    private BigDecimal quantidadeAtual;
    private BigDecimal quantidadeMinima;
    private String unidadeMedida;
    private String status; // "CRITICO", "ALERTA" ou "VENCENDO"
    private Double percentualRestante;
    private java.time.LocalDate dataValidade;

}
