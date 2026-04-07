package com.estoque.sistema.dto;

import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.TipoPedido;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private String identificacao;
    private Long mesaId;
    private String observacao;
    private TipoPedido tipoPedido;
    private FormaPagamento formaPagamento;
    private Boolean pago;
    private String clienteNome;
    private String clienteTelefone;
    private String enderecoEntrega;
    private java.math.BigDecimal taxaEntrega;
    private java.math.BigDecimal taxaServico;
    private List<ItemPedidoRequestDTO> itens;
}
