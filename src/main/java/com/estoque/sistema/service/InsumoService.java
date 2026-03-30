package com.estoque.sistema.service;

import com.estoque.sistema.dto.InsumoRequestDTO;
import com.estoque.sistema.dto.InsumoResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Insumo;
import com.estoque.sistema.repository.InsumoRepository;
import io.github.resilience4j.retry.annotation.Retry;
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
public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    @CacheEvict(value = "insumos-criticos", allEntries = true)
    public InsumoResponseDTO criar(@NonNull InsumoRequestDTO dto) {
        Insumo insumo = mapToEntity(dto);
        Insumo salvo = java.util.Objects.requireNonNull(insumoRepository.save(insumo));
        return mapToResponseDTO(salvo);
    }

    public Page<InsumoResponseDTO> listarTodos(@NonNull Pageable pageable) {
        return insumoRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    public Optional<InsumoResponseDTO> buscarPorId(@NonNull Long id) {
        return insumoRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Cacheable(value = "insumos-criticos")
    public List<InsumoResponseDTO> listarCriticos() {
        return insumoRepository.findAllCriticos().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "insumos-criticos", allEntries = true)
    public InsumoResponseDTO atualizar(@NonNull Long id, @NonNull InsumoRequestDTO dto) {
        return insumoRepository.findById(id).map(insumo -> {
            insumo.setNome(dto.getNome());
            insumo.setQuantidade(dto.getQuantidade());
            insumo.setUnidadeMedida(dto.getUnidadeMedida());
            insumo.setCustoUnitario(dto.getCustoUnitario());
            insumo.setQuantidadeMinima(dto.getQuantidadeMinima());
            return mapToResponseDTO(insumoRepository.save(insumo));
        }).orElseThrow(() -> new ResourceNotFoundException("Insumo não localizado."));
    }

    @Transactional
    @CacheEvict(value = "insumos-criticos", allEntries = true)
    @Retry(name = "default")
    public InsumoResponseDTO adicionarLote(@NonNull Long id, @NonNull BigDecimal quantidadeAdicionada) {
        return insumoRepository.findById(id).map(insumo -> {
            insumo.setQuantidade(insumo.getQuantidade().add(quantidadeAdicionada));
            return mapToResponseDTO(insumoRepository.save(insumo));
        }).orElseThrow(() -> new ResourceNotFoundException("Insumo não localizado."));
    }

    @Transactional
    @CacheEvict(value = "insumos-criticos", allEntries = true)
    public void deletar(@NonNull Long id) {

        insumoRepository.deleteById(id);
    }

    private Insumo mapToEntity(InsumoRequestDTO dto) {
        Insumo insumo = new Insumo();
        insumo.setNome(dto.getNome());
        insumo.setQuantidade(dto.getQuantidade());
        insumo.setUnidadeMedida(dto.getUnidadeMedida());
        insumo.setCustoUnitario(dto.getCustoUnitario());
        insumo.setQuantidadeMinima(dto.getQuantidadeMinima());
        return insumo;
    }

    private InsumoResponseDTO mapToResponseDTO(Insumo insumo) {
        InsumoResponseDTO dto = new InsumoResponseDTO();
        dto.setId(insumo.getId());
        dto.setNome(insumo.getNome());
        dto.setQuantidade(insumo.getQuantidade());
        dto.setUnidadeMedida(insumo.getUnidadeMedida());
        dto.setCustoUnitario(insumo.getCustoUnitario());
        dto.setQuantidadeMinima(insumo.getQuantidadeMinima());
        dto.setCritico(insumo.getQuantidade().compareTo(insumo.getQuantidadeMinima()) <= 0);
        return dto;
    }
}
