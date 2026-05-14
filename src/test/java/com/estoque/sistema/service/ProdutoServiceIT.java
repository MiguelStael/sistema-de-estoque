package com.estoque.sistema.service;

import com.estoque.sistema.AbstractIntegrationTest;
import com.estoque.sistema.dto.ProdutoRequestDTO;
import com.estoque.sistema.dto.ProdutoResponseDTO;
import com.estoque.sistema.model.Categoria;
import com.estoque.sistema.repository.CategoriaRepository;
import com.estoque.sistema.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class ProdutoServiceIT extends AbstractIntegrationTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Long categoriaId;

    @BeforeEach
    void setup() {
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria cat = new Categoria();
        cat.setNome("Eletrônicos");
        cat.setAtiva(true);
        cat = categoriaRepository.save(cat);
        categoriaId = cat.getId();
    }

    @Test
    void deveCriarEBuscarProduto() {
        ProdutoRequestDTO request = new ProdutoRequestDTO();
        request.setNome("Mouse Gamer");
        request.setPreco(new BigDecimal("150.00"));
        request.setQuantidade(10);
        request.setCategoriaId(categoriaId);

        ProdutoResponseDTO response = produtoService.criarProduto(request, null);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getNome()).isEqualTo("Mouse Gamer");

        Optional<ProdutoResponseDTO> buscado = produtoService.buscarPorId(response.getId());
        assertThat(buscado).isPresent();
        assertThat(buscado.get().getNome()).isEqualTo("Mouse Gamer");
    }
}
