package com.estoque.sistema.service;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;
    private final HistoricoPedidoRepository historicoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setIdentificacao(request.getIdentificacao());
        pedido.setObservacao(request.getObservacao());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTipoPedido(request.getTipoPedido());

        // Delivery pode ser pago no ato da criação
        if (request.getTipoPedido() == TipoPedido.DELIVERY && Boolean.TRUE.equals(request.getPago())) {
            pedido.setPago(true);
            pedido.setFormaPagamento(request.getFormaPagamento());
            pedido.setDataPagamento(LocalDateTime.now());
        }

        List<ItemPedido> itens = request.getItens().stream().map(itemDto -> {
            Long produtoId = itemDto.getProdutoId();
            if (produtoId == null) throw new RuntimeException("ID do produto não informado.");
            
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + produtoId));

            validarEDeduzirEstoque(produto, itemDto.getQuantidade());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            return item;
        }).collect(Collectors.toList());

        pedido.setItens(itens);
        pedido.calcularTotal();

        Pedido salvo = pedidoRepository.save(pedido);
        registrarHistorico(salvo, "Pedido criado e estoque de insumos reservado.");

        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        String logDetalhe = "Status alterado para: " + novoStatus;
        if (motivo != null && !motivo.isBlank()) {
            logDetalhe += " - Motivo: " + motivo;
        }

        if (novoStatus == StatusPedido.CANCELADO && pedido.getStatus() != StatusPedido.CANCELADO) {
            estornarEstoque(pedido);
            logDetalhe += " (Estoque de insumos estornado)";
        }

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);
        registrarHistorico(salvo, logDetalhe);

        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO pagarPedido(Long id, FormaPagamento forma) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        if (pedido.getPago()) {
            throw new RuntimeException("Pedido já está pago.");
        }

        pedido.setPago(true);
        pedido.setFormaPagamento(forma);
        pedido.setDataPagamento(LocalDateTime.now());
        
        Pedido salvo = pedidoRepository.save(pedido);
        registrarHistorico(salvo, "Pagamento efetuado via: " + forma);
        
        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO editarPedido(Long id, PedidoRequestDTO request, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Não é possível editar um pedido encerrado ou cancelado.");
        }

        BigDecimal totalAnterior = pedido.getTotal();
        
        // Estorna o estoque atual para reprocessar
        estornarEstoque(pedido);
        pedido.getItens().clear();

        List<ItemPedido> novosItens = request.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + itemDto.getProdutoId()));

            validarEDeduzirEstoque(produto, itemDto.getQuantidade());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            return item;
        }).collect(Collectors.toList());

        pedido.setItens(novosItens);
        pedido.setIdentificacao(request.getIdentificacao());
        pedido.setObservacao(request.getObservacao());
        pedido.setTipoPedido(request.getTipoPedido());
        pedido.calcularTotal();

        BigDecimal novoTotal = pedido.getTotal();
        String logDetalhe = "Pedido editado.";
        if (novoTotal.compareTo(totalAnterior) != 0) {
            logDetalhe += " Ajuste financeiro: de " + totalAnterior + " para " + novoTotal;
        }
        if (motivo != null && !motivo.isBlank()) {
            logDetalhe += " - Motivo: " + motivo;
        }

        Pedido salvo = pedidoRepository.save(pedido);
        registrarHistorico(salvo, logDetalhe);

        return mapToResponse(salvo);
    }

    public List<PedidoResponseDTO> listarFilaAtiva() {
        List<StatusPedido> encerrados = Arrays.asList(StatusPedido.ENTREGUE, StatusPedido.CANCELADO);
        return pedidoRepository.findFilaAtiva(encerrados).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RelatorioFaturamentoDTO gerarFechamentoMensal(int mes, int ano) {
        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fim = inicio.plusMonths(1).minusNanos(1);

        List<com.estoque.sistema.dto.RelatorioAgregadoDTO> agregados = pedidoRepository.findFaturamentoAgregado(inicio, fim);
        
        BigDecimal faturamentoTotal = agregados.stream()
                .map(com.estoque.sistema.dto.RelatorioAgregadoDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> faturamentoPorTipo = agregados.stream()
                .collect(Collectors.groupingBy(
                        a -> a.tipoPedido().name(),
                        Collectors.mapping(com.estoque.sistema.dto.RelatorioAgregadoDTO::valor, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        Map<String, BigDecimal> faturamentoPorForma = agregados.stream()
                .filter(a -> a.formaPagamento() != null)
                .collect(Collectors.groupingBy(
                        a -> a.formaPagamento().name(),
                        Collectors.mapping(com.estoque.sistema.dto.RelatorioAgregadoDTO::valor, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        org.springframework.data.domain.Page<Pedido> pedidosAuditadosPage = pedidoRepository.findByDataCriacaoBetween(inicio, fim, org.springframework.data.domain.PageRequest.of(0, 50));
        org.springframework.data.domain.Page<PedidoResponseDTO> pedidosAuditados = pedidosAuditadosPage.map(this::mapToResponse);

        List<Object[]> resultados = itemPedidoRepository.findProdutosMaisVendidos(inicio, fim);
        List<Map<String, Object>> itensRanking = resultados.stream().map(res -> {
            Map<String, Object> map = new HashMap<>();
            map.put("produto", res[0]);
            map.put("quantidade", res[1]);
            return map;
        }).collect(Collectors.toList());

        return RelatorioFaturamentoDTO.builder()
                .faturamentoTotal(faturamentoTotal)
                .faturamentoPorTipo(faturamentoPorTipo)
                .faturamentoPorForma(faturamentoPorForma)
                .pedidosAuditados(pedidosAuditados)
                .itensMaisVendidos(itensRanking)
                .build();
    }

    private void validarEDeduzirEstoque(Produto produto, Integer quantidadePedido) {
        // 1. Se o produto tiver quantidade direta (ex: bebida pronta), deduzir do produto
        if (produto.getQuantidade() != null && produto.getQuantidade() > 0) {
            if (produto.getQuantidade() < quantidadePedido) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidade(produto.getQuantidade() - quantidadePedido);
            produtoRepository.save(produto);
        }

        // 2. Deduzir de cada insumo da Ficha Técnica
        if (produto.getItensFicha() != null && !produto.getItensFicha().isEmpty()) {
            List<Insumo> insumosParaSalvar = new ArrayList<>();
            for (ItemFichaTecnica itemFicha : produto.getItensFicha()) {
                Insumo insumo = itemFicha.getInsumo();
                BigDecimal quantidadeNecessaria = itemFicha.getQuantidade().multiply(new BigDecimal(quantidadePedido));
                
                if (insumo.getQuantidade().compareTo(quantidadeNecessaria) < 0) {
                    throw new RuntimeException("Estoque insuficiente do insumo [" + insumo.getNome() + 
                                               "] para produzir o prato [" + produto.getNome() + "].");
                }
                
                insumo.setQuantidade(insumo.getQuantidade().subtract(quantidadeNecessaria));
                insumosParaSalvar.add(insumo);
            }
            if (!insumosParaSalvar.isEmpty()) {
                insumoRepository.saveAll(insumosParaSalvar);
            }
        }
    }

    private void estornarEstoque(Pedido pedido) {
        List<Produto> produtosParaEstornar = new ArrayList<>();
        List<Insumo> insumosParaEstornar = new ArrayList<>();

        pedido.getItens().forEach(item -> {
            Produto produto = item.getProduto();
            Integer qtdPedido = item.getQuantidade();

            // Estorna quantidade do produto
            if (produto.getQuantidade() != null) {
                produto.setQuantidade(produto.getQuantidade() + qtdPedido);
                produtosParaEstornar.add(produto);
            }

            // Estorna insumos da Ficha Técnica
            if (produto.getItensFicha() != null) {
                for (ItemFichaTecnica itemFicha : produto.getItensFicha()) {
                    Insumo insumo = itemFicha.getInsumo();
                    BigDecimal quantidadeParaEstornar = itemFicha.getQuantidade().multiply(new BigDecimal(qtdPedido));
                    insumo.setQuantidade(insumo.getQuantidade().add(quantidadeParaEstornar));
                    insumosParaEstornar.add(insumo);
                }
            }
        });

        if (!produtosParaEstornar.isEmpty()) {
            produtoRepository.saveAll(produtosParaEstornar);
        }
        if (!insumosParaEstornar.isEmpty()) {
            insumoRepository.saveAll(insumosParaEstornar);
        }
    }

    private void registrarHistorico(Pedido pedido, String descricao) {
        HistoricoPedido historico = new HistoricoPedido();
        historico.setPedido(pedido);
        historico.setStatus(pedido.getStatus());
        historico.setDescricao(descricao);
        historicoRepository.save(historico);
    }

    private PedidoResponseDTO mapToResponse(Pedido p) {
        return PedidoResponseDTO.builder()
                .id(p.getId())
                .identificacao(p.getIdentificacao())
                .dataCriacao(p.getDataCriacao())
                .status(p.getStatus())
                .tipoPedido(p.getTipoPedido())
                .formaPagamento(p.getFormaPagamento())
                .pago(p.getPago())
                .dataPagamento(p.getDataPagamento())
                .total(p.getTotal())
                .observacao(p.getObservacao())
                .itens(p.getItens().stream().map(item -> ItemPedidoResponseDTO.builder()
                        .produtoId(item.getProduto().getId())
                        .produtoNome(item.getProduto().getNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .subtotal(item.getSubtotal())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
