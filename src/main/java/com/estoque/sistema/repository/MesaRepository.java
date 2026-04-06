package com.estoque.sistema.repository;

import com.estoque.sistema.model.Mesa;
import com.estoque.sistema.model.StatusMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByNumero(Integer numero);
    List<Mesa> findAllByAtivaTrue();
    List<Mesa> findAllByStatus(StatusMesa status);
}
