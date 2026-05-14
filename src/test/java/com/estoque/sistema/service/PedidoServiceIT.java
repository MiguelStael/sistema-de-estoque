package com.estoque.sistema.service;

import com.estoque.sistema.AbstractIntegrationTest;
import com.estoque.sistema.dto.PedidoRequestDTO;
import com.estoque.sistema.dto.PedidoResponseDTO;
import com.estoque.sistema.dto.ItemPedidoRequestDTO;
import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class PedidoServiceIT extends AbstractIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Long produtoId;

    @BeforeEach
    void setup() {
        categoriaRepository.deleteAll();
        produtoRepository.deleteAll();
        ingredienteRepository.deleteAll();
        mesaRepository.deleteAll();

        Categoria cat = new Categoria();
        cat.setNome("Teste");
        cat.setAtiva(true);
        cat = categoriaRepository.save(cat);

        Ingrediente ing = new Ingrediente();
        ing.setNome("Tomate");
        ing.setQuantidade(new BigDecimal("10.000"));
        ing.setUnidadeMedida(UnidadeMedida.KG);
        ing.setAtivo(true);
        ing = ingredienteRepository.save(ing);

        Produto prod = new Produto();
        prod.setNome("Salada");
        prod.setPreco(new BigDecimal("20.00"));
        prod.setQuantidade(10);
        prod.setAtivo(true);
        prod.setDisponivel(true);
        prod.setCategoria(cat);
        
        Composicao comp = new Composicao();
        comp.setProduto(prod);
        comp.setIngrediente(ing);
        comp.setQuantidade(new BigDecimal("0.500"));
        prod.getItensComposicao().add(comp);
        
        prod = produtoRepository.save(prod);
        produtoId = prod.getId();

        Mesa mesa = new Mesa();
        mesa.setNumero(1);
        mesa.setCapacidade(4);
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setAtiva(true);
        mesaRepository.save(mesa);
    }

    @Test
    void deveCriarPedidoEDeduzirEstoque() {
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setIdentificacao("Mesa 1");
        request.setTipoPedido(TipoPedido.PRESENCIAL);
        request.setMesaId(mesaRepository.findAll().get(0).getId());
        
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produtoId);
        item.setQuantidade(2);
        request.setItens(List.of(item));

        PedidoResponseDTO response = pedidoService.criarPedido(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTotal()).isEqualByComparingTo("40.00");

        Ingrediente ing = ingredienteRepository.findAll().get(0);
        assertThat(ing.getQuantidade()).isEqualByComparingTo("9.000"); // 10 - (0.5 * 2)
    }

    @Test
    void deveCancelarPedidoEEstornarEstoque() {
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setIdentificacao("Test Estorno");
        request.setTipoPedido(TipoPedido.DELIVERY);
        ItemPedidoRequestDTO item = new ItemPedidoRequestDTO();
        item.setProdutoId(produtoId);
        item.setQuantidade(1);
        request.setItens(List.of(item));

        PedidoResponseDTO criado = pedidoService.criarPedido(request);
        
        Ingrediente ingAntes = ingredienteRepository.findAll().get(0);
        assertThat(ingAntes.getQuantidade()).isEqualByComparingTo("9.500");

        pedidoService.atualizarStatus(criado.getId(), StatusPedido.CANCELADO, "Erro no pedido");

        Ingrediente ingDepois = ingredienteRepository.findAll().get(0);
        assertThat(ingDepois.getQuantidade()).isEqualByComparingTo("10.000");
    }
}
