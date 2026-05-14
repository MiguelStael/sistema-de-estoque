package com.estoque.sistema.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemPedidoRepositoryCustom {
    List<Object[]> findProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim);
}
