package com.estoque.sistema.repository;

import com.estoque.sistema.model.HistoricoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoPedidoRepository extends JpaRepository<HistoricoPedido, Long> {
    List<HistoricoPedido> findByPedidoIdOrderByDataRegistroDesc(Long pedidoId);
}
