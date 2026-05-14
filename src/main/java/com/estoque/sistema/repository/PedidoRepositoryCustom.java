package com.estoque.sistema.repository;

import com.estoque.sistema.dto.RelatorioAgregadoDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepositoryCustom {
    List<RelatorioAgregadoDTO> findFaturamentoAgregado(LocalDateTime inicio, LocalDateTime fim);
}
