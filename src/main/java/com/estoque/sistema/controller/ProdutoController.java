package com.estoque.sistema.controller;

import com.estoque.sistema.model.Produto;
import com.estoque.sistema.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/cardapio")
    public ResponseEntity<List<Produto>> cardapioPublico() {
        return ResponseEntity.ok(produtoService.listarCardapioPublico());
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/produtos/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable @org.springframework.lang.NonNull Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/produtos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> criar(
            @RequestParam("nome") String nome,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("preco") BigDecimal preco,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem
    ) {
        Produto novo = new Produto();
        novo.setNome(nome);
        novo.setDescricao(descricao);
        novo.setPreco(preco);
        novo.setQuantidade(quantidade);
        novo.setCategoria(com.estoque.sistema.model.CategoriaProduto.valueOf(categoria.toUpperCase()));

        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criarProduto(novo, imagem));
    }

    @PutMapping(value = "/produtos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> atualizar(
            @PathVariable @org.springframework.lang.NonNull Long id,
            @RequestParam("nome") String nome,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("preco") BigDecimal preco,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem
    ) {
        Produto dados = new Produto();
        dados.setNome(nome);
        dados.setDescricao(descricao);
        dados.setPreco(preco);
        dados.setQuantidade(quantidade);
        dados.setCategoria(com.estoque.sistema.model.CategoriaProduto.valueOf(categoria.toUpperCase()));

        try {
            return ResponseEntity.ok(produtoService.atualizarProduto(id, dados, imagem));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/produtos/{id}/disponibilidade")
    public ResponseEntity<Produto> alterarDisponibilidade(
            @PathVariable @org.springframework.lang.NonNull Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean disponivel = Boolean.TRUE.equals(body.getOrDefault("disponivel", false));
        return ResponseEntity.ok(produtoService.alterarDisponibilidade(id, disponivel));
    }

    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<Void> deletar(@PathVariable @org.springframework.lang.NonNull Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
