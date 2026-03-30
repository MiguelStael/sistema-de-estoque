package com.estoque.sistema.repository;

import com.estoque.sistema.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    
    @Query("SELECT ip.produto.nome, SUM(ip.quantidade) as totalVendido " +
           "FROM ItemPedido ip JOIN ip.pedido p " +
           "WHERE p.status = 'ENTREGUE' AND p.dataCriacao BETWEEN :inicio AND :fim " +
           "GROUP BY ip.produto.nome " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProdutosMaisVendidos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
