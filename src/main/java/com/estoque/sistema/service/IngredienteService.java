package com.estoque.sistema.service;

import com.estoque.sistema.dto.IngredienteRequestDTO;
import com.estoque.sistema.dto.IngredienteResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.repository.IngredienteRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final MovimentacaoService movimentacaoService;

    public IngredienteService(IngredienteRepository ingredienteRepository, MovimentacaoService movimentacaoService) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimentacaoService = movimentacaoService;
    }

    @Transactional
    @CacheEvict(value = "ingredientes-criticos", allEntries = true)
    public IngredienteResponseDTO criar(@NonNull IngredienteRequestDTO dto) {
        Ingrediente ingrediente = mapToEntity(dto);
        Ingrediente salvo = ingredienteRepository.save(ingrediente);
        return mapToResponseDTO(salvo);
    }

    public Page<IngredienteResponseDTO> listarTodos(@NonNull Pageable pageable) {
        return ingredienteRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public Optional<IngredienteResponseDTO> buscarPorId(@NonNull Long id) {
        return ingredienteRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Cacheable(value = "ingredientes-criticos")
    public List<IngredienteResponseDTO> listarCriticos() {
        return ingredienteRepository.findAllCriticos().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "ingredientes-criticos", allEntries = true)
    public IngredienteResponseDTO atualizar(@NonNull Long id, @NonNull IngredienteRequestDTO dto) {
        return ingredienteRepository.findById(id).map(ingrediente -> {
            ingrediente.setNome(dto.getNome());
            ingrediente.setQuantidade(dto.getQuantidade());
            ingrediente.setUnidadeMedida(dto.getUnidadeMedida());
            ingrediente.setCustoUnitario(dto.getCustoUnitario());
            ingrediente.setQuantidadeMinima(dto.getQuantidadeMinima());
            ingrediente.setDataValidade(dto.getDataValidade());
            return mapToResponseDTO(ingredienteRepository.save(ingrediente));
        }).orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao localizado."));
    }

    @Transactional
    @CacheEvict(value = "ingredientes-criticos", allEntries = true)
    public IngredienteResponseDTO adicionarLote(@NonNull Long id, @NonNull BigDecimal quantidadeAdicionada) {
        return ingredienteRepository.findById(id).map(ingrediente -> {
            ingrediente.setQuantidade(ingrediente.getQuantidade().add(quantidadeAdicionada));
            Ingrediente salvo = ingredienteRepository.save(ingrediente);
            movimentacaoService.registrarAjuste(salvo, quantidadeAdicionada, "Entrada de lote/compras");
            return mapToResponseDTO(salvo);
        }).orElseThrow(() -> new ResourceNotFoundException("Ingrediente nao localizado."));
    }

    @Transactional
    @CacheEvict(value = "ingredientes-criticos", allEntries = true)
    public void deletar(@NonNull Long id) {
        ingredienteRepository.deleteById(id);
    }

    private Ingrediente mapToEntity(IngredienteRequestDTO dto) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNome(dto.getNome());
        ingrediente.setQuantidade(dto.getQuantidade());
        ingrediente.setUnidadeMedida(dto.getUnidadeMedida());
        ingrediente.setCustoUnitario(dto.getCustoUnitario());
        ingrediente.setQuantidadeMinima(dto.getQuantidadeMinima());
        ingrediente.setDataValidade(dto.getDataValidade());
        return ingrediente;
    }

    private IngredienteResponseDTO mapToResponseDTO(Ingrediente ingrediente) {
        IngredienteResponseDTO dto = new IngredienteResponseDTO();
        dto.setId(ingrediente.getId());
        dto.setNome(ingrediente.getNome());
        dto.setQuantidade(ingrediente.getQuantidade());
        dto.setUnidadeMedida(ingrediente.getUnidadeMedida());
        dto.setCustoUnitario(ingrediente.getCustoUnitario());
        dto.setQuantidadeMinima(ingrediente.getQuantidadeMinima());
        dto.setDataValidade(ingrediente.getDataValidade());
        dto.setCritico(ingrediente.getQuantidade().compareTo(ingrediente.getQuantidadeMinima()) <= 0);
        return dto;
    }
}
