package com.estoque.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço não pode ser negativo")
    private BigDecimal preco;

    @NotNull(message = "A quantidade inicial é obrigatória")
    @PositiveOrZero(message = "A quantidade inicial não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;

    private Integer quantidadeMinima;

    private boolean removerImagem = false;

    private List<ComposicaoRequestDTO> itensComposicao = new ArrayList<>();
}
