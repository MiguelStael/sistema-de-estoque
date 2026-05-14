package com.estoque.sistema.repository;

import com.estoque.sistema.model.TipoMovimentacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MovimentacaoRepositoryCustom {
    BigDecimal sumTotalPerdas(TipoMovimentacao tipo, LocalDateTime inicio, LocalDateTime fim);
}
