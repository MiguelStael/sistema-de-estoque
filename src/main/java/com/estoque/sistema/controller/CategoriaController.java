package com.estoque.sistema.controller;

import com.estoque.sistema.dto.CategoriaRequestDTO;
import com.estoque.sistema.dto.CategoriaResponseDTO;
import com.estoque.sistema.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody @Valid CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(dto));
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequestDTO dto) {
        return categoriaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
    }
}
