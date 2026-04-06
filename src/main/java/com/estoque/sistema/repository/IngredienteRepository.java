package com.estoque.sistema.repository;

import com.estoque.sistema.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    @Query("SELECT i FROM Ingrediente i WHERE i.quantidadeMinima IS NOT NULL AND i.quantidade <= i.quantidadeMinima")
    List<Ingrediente> findAllCriticos();
}
