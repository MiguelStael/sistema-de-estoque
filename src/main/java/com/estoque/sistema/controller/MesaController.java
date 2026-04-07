package com.estoque.sistema.controller;

import com.estoque.sistema.dto.MesaRequestDTO;
import com.estoque.sistema.dto.MesaResponseDTO;
import com.estoque.sistema.service.MesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public List<MesaResponseDTO> listarTodas() {
        return mesaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<MesaResponseDTO> criar(@RequestBody @Valid MesaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.criar(dto));
    }

    @PutMapping("/{id}")
    public MesaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid MesaRequestDTO dto) {
        return mesaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        mesaService.deletar(id);
    }
}
