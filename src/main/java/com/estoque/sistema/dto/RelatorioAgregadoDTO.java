package com.estoque.sistema.dto;

import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.TipoPedido;
import java.math.BigDecimal;

public record RelatorioAgregadoDTO(
    BigDecimal valor,
    TipoPedido tipoPedido,
    FormaPagamento formaPagamento
) {}
