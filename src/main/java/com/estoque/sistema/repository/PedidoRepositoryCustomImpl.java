package com.estoque.sistema.repository;

import com.estoque.sistema.dto.RelatorioAgregadoDTO;
import com.estoque.sistema.model.Pedido;
import com.estoque.sistema.model.StatusPedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoRepositoryCustomImpl implements PedidoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RelatorioAgregadoDTO> findFaturamentoAgregado(LocalDateTime inicio, LocalDateTime fim) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RelatorioAgregadoDTO> query = cb.createQuery(RelatorioAgregadoDTO.class);
        Root<Pedido> pedido = query.from(Pedido.class);

        query.multiselect(
                cb.sum(pedido.get("total")),
                pedido.get("tipoPedido"),
                pedido.get("formaPagamento")
        );

        query.where(cb.and(
                cb.between(pedido.get("dataCriacao"), inicio, fim),
                cb.equal(pedido.get("status"), StatusPedido.ENTREGUE),
                cb.equal(pedido.get("pago"), true)
        ));

        query.groupBy(pedido.get("tipoPedido"), pedido.get("formaPagamento"));

        return entityManager.createQuery(query).getResultList();
    }
}
