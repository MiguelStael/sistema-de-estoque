package com.estoque.sistema.repository;

import com.estoque.sistema.model.Movimentacao;
import com.estoque.sistema.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("SELECT SUM(m.quantidade * m.valorUnitarioCusto) FROM Movimentacao m WHERE m.tipo = :tipo AND m.dataMovimentacao BETWEEN :inicio AND :fim")
    BigDecimal sumTotalPerdas(@Param("tipo") TipoMovimentacao tipo, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<Movimentacao> findByIngredienteIdOrderByDataMovimentacaoDesc(Long ingredienteId);
}
