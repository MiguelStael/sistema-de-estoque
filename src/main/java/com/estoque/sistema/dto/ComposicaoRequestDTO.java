package com.estoque.sistema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComposicaoRequestDTO {

    @NotNull(message = "O ID do ingrediente é obrigatório")
    private Long ingredienteId;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;
}
