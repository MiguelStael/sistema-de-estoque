package com.estoque.sistema.service;

import com.estoque.sistema.model.Produto;
import com.estoque.sistema.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ImageStorageService imageStorageService;

    public ProdutoService(ProdutoRepository produtoRepository, ImageStorageService imageStorageService) {
        this.produtoRepository = produtoRepository;
        this.imageStorageService = imageStorageService;
    }

    public Produto criarProduto(@org.springframework.lang.NonNull Produto produto, MultipartFile imagem) {
        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = imageStorageService.storeFile(imagem);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/imagens/")
                    .path(nomeArquivo)
                    .toUriString();
            produto.setUrlImagem(fileDownloadUri);
        }
        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarCardapioPublico() {
        return produtoRepository.findAllByDisponivelTrue();
    }

    public Optional<Produto> buscarPorId(@org.springframework.lang.NonNull Long id) {
        return produtoRepository.findById(id);
    }

    public Produto alterarDisponibilidade(@org.springframework.lang.NonNull Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não localizado."));
        produto.setDisponivel(disponivel);
        return produtoRepository.save(produto);
    }

    public void deletarProduto(@org.springframework.lang.NonNull Long id) {
        produtoRepository.deleteById(id);
    }

    public Produto atualizarProduto(@org.springframework.lang.NonNull Long id, @org.springframework.lang.NonNull Produto produtoAtualizado, MultipartFile novaImagem) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidade(produtoAtualizado.getQuantidade());
            produto.setCategoria(produtoAtualizado.getCategoria());

            if (novaImagem != null && !novaImagem.isEmpty()) {
                String nomeArquivo = imageStorageService.storeFile(novaImagem);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/imagens/")
                        .path(nomeArquivo)
                        .toUriString();
                produto.setUrlImagem(fileDownloadUri);
            }

            return produtoRepository.save(produto);
        }).orElseThrow(() -> new RuntimeException("Produto não localizado."));
    }
}
