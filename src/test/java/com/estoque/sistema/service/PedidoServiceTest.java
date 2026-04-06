package com.estoque.sistema.service;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private IngredienteRepository ingredienteRepository;
    @Mock
    private PedidoLogRepository pedidoLogRepository;
    @Mock
    private ItemPedidoRepository itemPedidoRepository;
    @Mock
    private MesaRepository mesaRepository;
    @Mock
    private MovimentacaoService movimentacaoService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("Deve criar pedido com sucesso e deduzir estoque de ingrediente")
    void criarPedido_ComSucesso_DeduzEstoque() {
        Ingrediente carne = new Ingrediente();
        carne.setId(1L);
        carne.setNome("Carne");
        carne.setQuantidade(new BigDecimal("10.000"));

        Produto hamburgo = new Produto();
        hamburgo.setId(1L);
        hamburgo.setNome("Hambúrguer");
        hamburgo.setPreco(new BigDecimal("25.00"));
        
        Composicao itemComposicao = new Composicao();
        itemComposicao.setIngrediente(carne);
        itemComposicao.setQuantidade(new BigDecimal("0.200"));
        hamburgo.getItensComposicao().add(itemComposicao);

        Mesa mesa = new Mesa();
        mesa.setId(1L);
        mesa.setNumero(1);
        mesa.setStatus(StatusMesa.LIVRE);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setTipoPedido(TipoPedido.PRESENCIAL);
        request.setMesaId(1L);
        
        ItemPedidoRequestDTO itemReq = new ItemPedidoRequestDTO();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(2);
        request.setItens(List.of(itemReq));

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(hamburgo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        PedidoResponseDTO response = pedidoService.criarPedido(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("9.600"), carne.getQuantidade());
        assertEquals(StatusMesa.OCUPADA, mesa.getStatus());
        
        // Corrigindo aviso de tipo nulo no verify
        List<Ingrediente> ingredientes = anyList();
        verify(ingredienteRepository, times(1)).saveAll(ingredientes);
        
        Pedido p = any(Pedido.class);
        verify(pedidoRepository, times(1)).save(p);
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque de ingrediente for insuficiente")
    void criarPedido_EstoqueInsuficiente_LancaExcecao() {
        Ingrediente carne = new Ingrediente();
        carne.setNome("Carne");
        carne.setQuantidade(new BigDecimal("0.100"));

        Produto hamburgo = new Produto();
        hamburgo.setId(1L);
        
        Composicao itemComposicao = new Composicao();
        itemComposicao.setIngrediente(carne);
        itemComposicao.setQuantidade(new BigDecimal("0.200"));
        hamburgo.getItensComposicao().add(itemComposicao);

        PedidoRequestDTO request = new PedidoRequestDTO();
        ItemPedidoRequestDTO itemReq = new ItemPedidoRequestDTO();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(1);
        request.setItens(List.of(itemReq));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(hamburgo));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> pedidoService.criarPedido(request));
        assertTrue(exception.getMessage().contains("Estoque insuficiente do ingrediente"));
        
        Pedido p = any();
        verify(pedidoRepository, never()).save(p);
    }

    @Test
    @DisplayName("Deve estornar estoque corretamente ao cancelar pedido")
    void estornarEstoque_AoCancelar_DevolveItens() {
        Ingrediente queijo = new Ingrediente();
        queijo.setQuantidade(new BigDecimal("1.000"));

        Produto pizza = new Produto();
        Composicao composicao = new Composicao();
        composicao.setIngrediente(queijo);
        composicao.setQuantidade(new BigDecimal("0.100"));
        pizza.getItensComposicao().add(composicao);

        Mesa mesa = new Mesa();
        mesa.setStatus(StatusMesa.OCUPADA);

        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setMesa(mesa);
        
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(pizza);
        item.setQuantidade(2);
        pedido.setItens(List.of(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.atualizarStatus(1L, StatusPedido.CANCELADO, "Cliente desistiu");

        assertEquals(new BigDecimal("1.200"), queijo.getQuantidade());
        assertEquals(StatusMesa.LIVRE, mesa.getStatus());
        
        List<Ingrediente> ingredientes = anyList();
        verify(ingredienteRepository, times(1)).saveAll(ingredientes);
    }
}
