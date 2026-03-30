package com.estoque.sistema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemFichaTecnicaRequestDTO {

    @NotNull(message = "O ID do insumo é obrigatório")
    private Long insumoId;

    @NotNull(message = "A quantidade do insumo é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;
}
