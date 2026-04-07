package com.estoque.sistema.repository;

import com.estoque.sistema.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByDisponivelTrue();

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Produto p WHERE p.quantidadeMinima IS NOT NULL AND p.quantidade <= p.quantidadeMinima")
    List<Produto> findAllCriticos();
}
