package com.estoque.sistema.repository;

import com.estoque.sistema.model.Movimentacao;
import com.estoque.sistema.model.TipoMovimentacao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoRepositoryCustomImpl implements MovimentacaoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigDecimal sumTotalPerdas(TipoMovimentacao tipo, LocalDateTime inicio, LocalDateTime fim) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<Movimentacao> movimentacao = query.from(Movimentacao.class);

        query.select(cb.sum(cb.prod(movimentacao.get("quantidade"), movimentacao.get("valorUnitarioCusto"))));

        query.where(cb.and(
                cb.equal(movimentacao.get("tipo"), tipo),
                cb.between(movimentacao.get("dataMovimentacao"), inicio, fim)
        ));

        BigDecimal result = entityManager.createQuery(query).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
