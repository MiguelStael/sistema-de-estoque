package com.estoque.sistema.dto;

import com.estoque.sistema.model.StatusMesa;
import lombok.Data;

@Data
public class MesaRequestDTO {
    private Integer numero;
    private StatusMesa status;
    private Integer capacidade;
}
