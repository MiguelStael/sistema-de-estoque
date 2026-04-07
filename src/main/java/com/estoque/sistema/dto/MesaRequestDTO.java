package com.estoque.sistema.dto;

import com.estoque.sistema.model.StatusMesa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MesaRequestDTO {

    @NotNull(message = "O número da mesa é obrigatório")
    @Positive(message = "O número da mesa deve ser positivo")
    private Integer numero;

    private StatusMesa status;

    @NotNull(message = "A capacidade da mesa é obrigatória")
    @Positive(message = "A capacidade da mesa deve ser positiva")
    private Integer capacidade;
}
