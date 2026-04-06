package com.estoque.sistema.service;

import com.estoque.sistema.dto.CategoriaRequestDTO;
import com.estoque.sistema.dto.CategoriaResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Categoria;
import com.estoque.sistema.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAllByAtivaTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        return mapToResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        if (id == null) throw new IllegalArgumentException("O ID da categoria não pode ser nulo.");
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ID: " + id));
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        return mapToResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null) throw new IllegalArgumentException("O ID da categoria não pode ser nulo.");
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ID: " + id));
        categoria.setAtiva(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaResponseDTO mapToResponse(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setAtiva(categoria.getAtiva());
        return dto;
    }
}
