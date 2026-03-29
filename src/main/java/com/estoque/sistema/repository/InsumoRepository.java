package com.estoque.sistema.repository;

import com.estoque.sistema.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findAllByQuantidadeLessThanEqual(BigDecimal quantidade);
}
