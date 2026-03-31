package com.estoque.sistema.repository;

import com.estoque.sistema.model.Pedido;
import com.estoque.sistema.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatusNotInOrderByDataCriacaoAsc(List<StatusPedido> statuses);

    Page<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.status NOT IN :statuses ORDER BY p.dataCriacao ASC")
    List<Pedido> findFilaAtiva(@Param("statuses") List<StatusPedido> statuses);

    @Query("SELECT new com.estoque.sistema.dto.RelatorioAgregadoDTO(" +
           "SUM(p.total), p.tipoPedido, p.formaPagamento) " +
           "FROM Pedido p WHERE p.dataCriacao BETWEEN :inicio AND :fim " +
           "AND p.status = 'ENTREGUE' AND p.pago = true " +
           "GROUP BY p.tipoPedido, p.formaPagamento")
    List<com.estoque.sistema.dto.RelatorioAgregadoDTO> findFaturamentoAgregado(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
