package com.estoque.sistema.dto;

import lombok.Data;

@Data
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativa;
}
