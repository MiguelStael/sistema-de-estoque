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
    private String tipo;
    private BigDecimal quantidadeAtual;
    private BigDecimal quantidadeMinima;
    private String unidadeMedida;
    private String status;
    private Double percentualRestante;
    private java.time.LocalDate dataValidade;

}
