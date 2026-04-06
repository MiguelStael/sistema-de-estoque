package com.estoque.sistema.repository;

import com.estoque.sistema.model.PedidoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoLogRepository extends JpaRepository<PedidoLog, Long> {
    List<PedidoLog> findByPedidoIdOrderByDataRegistroDesc(Long pedidoId);
}
