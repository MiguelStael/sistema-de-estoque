package com.estoque.sistema.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComposicaoResponseDTO {
    private Long id;
    private Long ingredienteId;
    private String ingredienteNome;
    private BigDecimal quantidade;
}
