package com.estoque.sistema.repository;

import com.estoque.sistema.model.Pedido;
import com.estoque.sistema.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, PedidoRepositoryCustom {

    List<Pedido> findByStatusNotInOrderByDataCriacaoAsc(List<StatusPedido> statuses);

    Page<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
