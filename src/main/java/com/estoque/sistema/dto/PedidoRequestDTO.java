package com.estoque.sistema.dto;

import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.TipoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class PedidoRequestDTO {
    
    @NotBlank(message = "A identificação do pedido é obrigatória")
    private String identificacao;

    private Long mesaId;
    private String observacao;

    @NotNull(message = "O tipo do pedido é obrigatório")
    private TipoPedido tipoPedido;

    private FormaPagamento formaPagamento;
    private Boolean pago;

    private String clienteNome;
    private String clienteTelefone;
    private String enderecoEntrega;

    @PositiveOrZero(message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntrega;

    @PositiveOrZero(message = "A taxa de serviço não pode ser negativa")
    private BigDecimal taxaServico;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;
}
