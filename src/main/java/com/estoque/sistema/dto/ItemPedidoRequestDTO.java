package com.estoque.sistema.dto;

import lombok.Data;

@Data
public class ItemPedidoRequestDTO {
    private Long produtoId;
    private Integer quantidade;
}
