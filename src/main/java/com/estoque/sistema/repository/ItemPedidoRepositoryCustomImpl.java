package com.estoque.sistema.repository;

import com.estoque.sistema.model.ItemPedido;
import com.estoque.sistema.model.Pedido;
import com.estoque.sistema.model.StatusPedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class ItemPedidoRepositoryCustomImpl implements ItemPedidoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<ItemPedido> itemPedido = query.from(ItemPedido.class);
        Join<ItemPedido, Pedido> pedido = itemPedido.join("pedido");

        query.multiselect(
                itemPedido.get("produto").get("nome"),
                cb.sum(itemPedido.get("quantidade"))
        );

        query.where(cb.and(
                cb.equal(pedido.get("status"), StatusPedido.ENTREGUE),
                cb.between(pedido.get("dataCriacao"), inicio, fim)
        ));

        query.groupBy(itemPedido.get("produto").get("nome"));
        query.orderBy(cb.desc(cb.sum(itemPedido.get("quantidade"))));

        return entityManager.createQuery(query).getResultList();
    }
}
