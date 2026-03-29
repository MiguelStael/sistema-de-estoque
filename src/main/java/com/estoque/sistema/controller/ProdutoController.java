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

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProduto(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> criar(
            @RequestParam("nome") String nome,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("preco") BigDecimal preco,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem
    ) {
        Produto novo = new Produto();
        novo.setNome(nome);
        novo.setDescricao(descricao);
        novo.setPreco(preco);
        novo.setQuantidade(quantidade);

        Produto salvo = produtoService.criarProduto(novo, imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestParam("nome") String nome,
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam("preco") BigDecimal preco,
            @RequestParam("quantidade") Integer quantidade,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem
    ) {
        Produto atualizar = new Produto();
        atualizar.setNome(nome);
        atualizar.setDescricao(descricao);
        atualizar.setPreco(preco);
        atualizar.setQuantidade(quantidade);

        try {
            Produto salvo = produtoService.atualizarProduto(id, atualizar, imagem);
            return ResponseEntity.ok(salvo);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
