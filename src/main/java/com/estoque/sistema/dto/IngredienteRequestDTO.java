package com.estoque.sistema.dto;

import com.estoque.sistema.model.UnidadeMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredienteRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    private BigDecimal quantidade;

    @NotNull(message = "A unidade de medida é obrigatória")
    private UnidadeMedida unidadeMedida;

    @NotNull(message = "O custo unitário é obrigatório")
    @PositiveOrZero(message = "O custo unitário não pode ser negativo")
    private BigDecimal custoUnitario;

    @NotNull(message = "A quantidade mínima é obrigatória")
    @PositiveOrZero(message = "A quantidade mínima não pode ser negativa")
    private BigDecimal quantidadeMinima;
}
