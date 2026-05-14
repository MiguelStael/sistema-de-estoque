package com.estoque.sistema.repository;

import com.estoque.sistema.model.Ingrediente;
import java.util.List;

public interface IngredienteRepositoryCustom {
    List<Ingrediente> findAllCriticos();
}
