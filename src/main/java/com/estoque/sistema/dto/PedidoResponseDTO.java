package com.estoque.sistema.dto;

import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.StatusPedido;
import com.estoque.sistema.model.TipoPedido;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PedidoResponseDTO {
    private Long id;
    private String identificacao;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private TipoPedido tipoPedido;
    private FormaPagamento formaPagamento;
    private Boolean pago;
    private LocalDateTime dataPagamento;
    private BigDecimal total;
    private String observacao;
    private List<ItemPedidoResponseDTO> itens;
}
