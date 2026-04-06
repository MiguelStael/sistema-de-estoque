package com.estoque.sistema.service;

import com.estoque.sistema.dto.MesaRequestDTO;
import com.estoque.sistema.dto.MesaResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Mesa;
import com.estoque.sistema.model.StatusMesa;
import com.estoque.sistema.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    public List<MesaResponseDTO> listarTodas() {
        return mesaRepository.findAllByAtivaTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MesaResponseDTO criar(MesaRequestDTO dto) {
        if (mesaRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new RuntimeException("Mesa com número " + dto.getNumero() + " já existe.");
        }
        Mesa mesa = new Mesa();
        mesa.setNumero(dto.getNumero());
        mesa.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusMesa.LIVRE);
        mesa.setCapacidade(dto.getCapacidade() != null ? dto.getCapacidade() : 2);
        return mapToResponse(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaResponseDTO atualizar(Long id, MesaRequestDTO dto) {
        if (id == null) throw new IllegalArgumentException("O ID da mesa não pode ser nulo.");
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada ID: " + id));
        
        if (dto.getNumero() != null && !dto.getNumero().equals(mesa.getNumero())) {
            if (mesaRepository.findByNumero(dto.getNumero()).isPresent()) {
                throw new RuntimeException("Mesa com número " + dto.getNumero() + " já existe.");
            }
            mesa.setNumero(dto.getNumero());
        }
        
        if (dto.getStatus() != null) mesa.setStatus(dto.getStatus());
        if (dto.getCapacidade() != null) mesa.setCapacidade(dto.getCapacidade());
        
        return mapToResponse(mesaRepository.save(mesa));
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null) throw new IllegalArgumentException("O ID da mesa não pode ser nulo.");
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada ID: " + id));
        mesa.setAtiva(false);
        mesaRepository.save(mesa);
    }

    private MesaResponseDTO mapToResponse(Mesa mesa) {
        MesaResponseDTO dto = new MesaResponseDTO();
        dto.setId(mesa.getId());
        dto.setNumero(mesa.getNumero());
        dto.setStatus(mesa.getStatus());
        dto.setCapacidade(mesa.getCapacidade());
        dto.setAtiva(mesa.getAtiva());
        return dto;
    }
}
