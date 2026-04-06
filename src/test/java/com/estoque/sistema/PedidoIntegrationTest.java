package com.estoque.sistema;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.CategoriaRepository;
import com.estoque.sistema.repository.IngredienteRepository;
import com.estoque.sistema.repository.MesaRepository;
import com.estoque.sistema.repository.ProdutoRepository;
import com.estoque.sistema.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Test
    @DisplayName("Deve processar pedido completo com banco de dados real e validar estoque")
    @Transactional
    void fluxoCompletoPedido_ComBancoReal() {
        Ingrediente tomate = new Ingrediente();
        tomate.setNome("Tomate");
        tomate.setQuantidade(new BigDecimal("10.000"));
        tomate.setUnidadeMedida(UnidadeMedida.UNIDADE);
        tomate.setCustoUnitario(new BigDecimal("1.50"));
        tomate.setQuantidadeMinima(new BigDecimal("1.000"));
        tomate = ingredienteRepository.save(tomate);

        Categoria pratos = new Categoria();
        pratos.setNome("Pratos");
        pratos = categoriaRepository.save(pratos);

        Produto salada = new Produto();
        salada.setNome("Salada");
        salada.setPreco(new BigDecimal("15.00"));
        salada.setQuantidade(0);
        salada.setDisponivel(true);
        salada.setCategoria(pratos);
        
        Composicao item = new Composicao();
        item.setIngrediente(tomate);
        item.setQuantidade(new BigDecimal("2.000"));
        item.setProduto(salada);
        salada.getItensComposicao().add(item);
        produtoRepository.save(salada);

        Mesa mesa5 = new Mesa();
        mesa5.setNumero(5);
        mesa5.setStatus(StatusMesa.LIVRE);
        mesa5 = mesaRepository.save(mesa5);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setIdentificacao("Mesa 5");
        request.setMesaId(mesa5.getId());
        request.setTipoPedido(TipoPedido.PRESENCIAL);
        
        ItemPedidoRequestDTO itemReq = new ItemPedidoRequestDTO();
        itemReq.setProdutoId(salada.getId());
        itemReq.setQuantidade(2);
        request.setItens(List.of(itemReq));

        PedidoResponseDTO response = pedidoService.criarPedido(request);

        assertNotNull(response.getId());
        assertEquals(new BigDecimal("30.00"), response.getTotal());
        
        Long ingredienteId = tomate.getId();
        if (ingredienteId == null) {
            throw new IllegalStateException("ID do ingrediente não deveria ser nulo após salvar.");
        }
        Ingrediente tomateAposVenda = ingredienteRepository.findById(ingredienteId).orElseThrow();
        assertEquals(new BigDecimal("6.000"), tomateAposVenda.getQuantidade());
    }
}
