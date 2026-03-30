package com.estoque.sistema.repository;

import com.estoque.sistema.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByDisponivelTrue();

    Page<Produto> findAllByDisponivelTrue(Pageable pageable);
}
