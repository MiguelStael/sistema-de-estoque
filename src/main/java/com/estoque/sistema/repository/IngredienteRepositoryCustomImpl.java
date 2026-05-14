package com.estoque.sistema.repository;

import com.estoque.sistema.model.Ingrediente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class IngredienteRepositoryCustomImpl implements IngredienteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ingrediente> findAllCriticos() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ingrediente> query = cb.createQuery(Ingrediente.class);
        Root<Ingrediente> ingrediente = query.from(Ingrediente.class);

        query.select(ingrediente)
             .where(cb.and(
                 cb.isNotNull(ingrediente.get("quantidadeMinima")),
                 cb.lessThanOrEqualTo(ingrediente.get("quantidade"), ingrediente.get("quantidadeMinima"))
             ));

        return entityManager.createQuery(query).getResultList();
    }
}
