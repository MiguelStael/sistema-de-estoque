package com.estoque.sistema.service;

import com.estoque.sistema.model.Insumo;
import com.estoque.sistema.repository.InsumoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public Insumo criar(@org.springframework.lang.NonNull Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    public List<Insumo> listarTodos() {
        return insumoRepository.findAll();
    }

    public Optional<Insumo> buscarPorId(@org.springframework.lang.NonNull Long id) {
        return insumoRepository.findById(id);
    }

    public Insumo atualizar(@org.springframework.lang.NonNull Long id, @org.springframework.lang.NonNull Insumo dados) {
        return insumoRepository.findById(id).map(insumo -> {
            insumo.setNome(dados.getNome());
            insumo.setUnidadeMedida(dados.getUnidadeMedida());
            insumo.setCustoUnitario(dados.getCustoUnitario());
            insumo.setQuantidadeMinima(dados.getQuantidadeMinima());
            return insumoRepository.save(insumo);
        }).orElseThrow(() -> new RuntimeException("Insumo não localizado."));
    }

    public Insumo adicionarLote(@org.springframework.lang.NonNull Long id, @org.springframework.lang.NonNull BigDecimal quantidadeAdicionada) {
        return insumoRepository.findById(id).map(insumo -> {
            insumo.setQuantidade(insumo.getQuantidade().add(quantidadeAdicionada));
            return insumoRepository.save(insumo);
        }).orElseThrow(() -> new RuntimeException("Insumo não localizado."));
    }

    public List<Insumo> listarCriticos() {
        return insumoRepository.findAll().stream()
                .filter(i -> i.getQuantidadeMinima() != null
                        && i.getQuantidade().compareTo(i.getQuantidadeMinima()) <= 0)
                .toList();
    }

    public void deletar(@org.springframework.lang.NonNull Long id) {
        insumoRepository.deleteById(id);
    }
}
