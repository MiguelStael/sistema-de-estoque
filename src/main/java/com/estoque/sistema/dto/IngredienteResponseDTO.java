package com.estoque.sistema.dto;

import com.estoque.sistema.model.UnidadeMedida;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredienteResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal quantidade;
    private UnidadeMedida unidadeMedida;
    private BigDecimal custoUnitario;
    private BigDecimal quantidadeMinima;
    private Boolean critico;
    private java.time.LocalDate dataValidade;
}
