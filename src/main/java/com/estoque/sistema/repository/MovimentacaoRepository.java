package com.estoque.sistema.repository;

import com.estoque.sistema.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long>, MovimentacaoRepositoryCustom {

    List<Movimentacao> findByIngredienteIdOrderByDataMovimentacaoDesc(Long ingredienteId);
}
