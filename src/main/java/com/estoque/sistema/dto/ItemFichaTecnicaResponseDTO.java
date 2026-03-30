package com.estoque.sistema.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemFichaTecnicaResponseDTO {
    private Long id;
    private Long insumoId;
    private String insumoNome;
    private BigDecimal quantidade;
}
