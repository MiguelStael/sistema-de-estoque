package com.estoque.sistema.controller;

import com.estoque.sistema.dto.ProdutoRequestDTO;
import com.estoque.sistema.dto.ProdutoResponseDTO;
import com.estoque.sistema.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estoque/produtos")
@Tag(name = "Estoque: Produtos", description = "Gerenciamento do cardápio e produtos prontos para venda")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/cardapio")
    @Operation(summary = "Listar cardápio público", description = "Retorna todos os produtos marcados como disponíveis para venda.")
    public ResponseEntity<List<ProdutoResponseDTO>> cardapioPublico() {
        return ResponseEntity.ok(produtoService.listarCardapioPublico());
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "nome") @NonNull Pageable pageable
    ) {
        return ResponseEntity.ok(produtoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable @NonNull Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar novo produto", description = "Cadastra um produto com foto, preco e composição de ingredientes.")
    public ResponseEntity<ProdutoResponseDTO> criar(
            @RequestPart("produto") @Valid @NonNull ProdutoRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(produtoService.criarProduto(dto, imagem));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable @NonNull Long id,
            @RequestPart("produto") @Valid @NonNull ProdutoRequestDTO dto,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem
    ) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, dto, imagem));
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Alterar disponibilidade", description = "Ativa ou desativa um produto do cardápio público temporariamente.")
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean disponivel = Boolean.TRUE.equals(body.getOrDefault("disponivel", false));
        return ResponseEntity.ok(produtoService.alterarDisponibilidade(id, disponivel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @NonNull Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}
