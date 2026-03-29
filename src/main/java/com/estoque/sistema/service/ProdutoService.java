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

    public Produto criarProduto(Produto produto, MultipartFile imagem) {
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

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    public Produto atualizarProduto(Long id, Produto produtoAtualizado, MultipartFile novaImagem) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidade(produtoAtualizado.getQuantidade());

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
