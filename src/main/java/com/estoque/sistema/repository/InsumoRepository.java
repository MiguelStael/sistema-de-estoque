package com.estoque.sistema.repository;

import com.estoque.sistema.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    @Query("SELECT i FROM Insumo i WHERE i.quantidadeMinima IS NOT NULL AND i.quantidade <= i.quantidadeMinima")
    List<Insumo> findAllCriticos();
}
