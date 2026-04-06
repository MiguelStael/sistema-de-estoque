package com.estoque.sistema.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    private String urlImagem;
    private Boolean disponivel;
    private CategoriaResponseDTO categoria;
    private List<ComposicaoResponseDTO> itensComposicao;
}
