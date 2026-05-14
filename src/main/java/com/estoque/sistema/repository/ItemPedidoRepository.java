package com.estoque.sistema.repository;

import com.estoque.sistema.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long>, ItemPedidoRepositoryCustom {

}
