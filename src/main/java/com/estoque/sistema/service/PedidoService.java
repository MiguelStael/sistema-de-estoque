package com.estoque.sistema.service;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final IngredienteRepository ingredienteRepository;
    private final PedidoLogRepository pedidoLogRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final MesaRepository mesaRepository;
    private final MovimentacaoService movimentacaoService;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuarioAutenticado() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return null;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    @org.springframework.retry.annotation.Retryable(retryFor = org.springframework.orm.ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @org.springframework.retry.annotation.Backoff(delay = 500))
    public PedidoResponseDTO criarPedido(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setIdentificacao(request.getIdentificacao());
        pedido.setObservacao(request.getObservacao());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setTipoPedido(request.getTipoPedido());
        
        pedido.setClienteNome(request.getClienteNome());
        pedido.setClienteTelefone(request.getClienteTelefone());
        pedido.setEnderecoEntrega(request.getEnderecoEntrega());
        pedido.setTaxaEntrega(request.getTaxaEntrega() != null ? request.getTaxaEntrega() : BigDecimal.ZERO);
        pedido.setTaxaServico(request.getTaxaServico() != null ? request.getTaxaServico() : BigDecimal.ZERO);

        if (request.getTipoPedido() == TipoPedido.PRESENCIAL && request.getMesaId() != null) {
            Long mesaId = java.util.Objects.requireNonNull(request.getMesaId(), "ID da mesa não pode ser nulo para pedido presencial.");
            Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada ID: " + mesaId));
            
            if (mesa.getStatus() == StatusMesa.OCUPADA) {
                throw new RuntimeException("Mesa " + mesa.getNumero() + " já está ocupada.");
            }
            
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);
            pedido.setMesa(mesa);
        }

        if (request.getTipoPedido() == TipoPedido.DELIVERY && Boolean.TRUE.equals(request.getPago())) {
            pedido.setPago(true);
            pedido.setFormaPagamento(request.getFormaPagamento());
            pedido.setDataPagamento(LocalDateTime.now());
        }

        List<ItemPedido> itens = request.getItens().stream().map(itemDto -> {
            Long produtoId = itemDto.getProdutoId();
            if (produtoId == null) throw new RuntimeException("ID do produto nao informado.");
            
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado ID: " + produtoId));

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
        registrarLog(salvo, "Pedido criado e estoque de ingredientes reservado.");

        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(@NonNull Long id, StatusPedido novoStatus, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado."));

        String logDetalhe = "Status alterado para: " + novoStatus;
        if (motivo != null && !motivo.isBlank()) {
            logDetalhe += " - Motivo: " + motivo;
        }

        if (novoStatus == StatusPedido.CANCELADO && pedido.getStatus() != StatusPedido.CANCELADO) {
            estornarEstoque(pedido);
            liberarMesa(pedido);
            logDetalhe += " (Estoque de ingredientes estornado e mesa liberada)";
        } else if (novoStatus == StatusPedido.ENTREGUE && pedido.getMesa() != null) {
            liberarMesa(pedido);
            logDetalhe += " (Mesa liberada)";
        }

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);
        registrarLog(salvo, logDetalhe);

        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO pagarPedido(@NonNull Long id, @NonNull FormaPagamento forma) {
        java.util.Objects.requireNonNull(id, "ID não pode ser nulo.");
        java.util.Objects.requireNonNull(forma, "Forma de pagamento não pode ser nula.");
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado."));

        if (pedido.getPago()) {
            throw new RuntimeException("Pedido ja esta pago.");
        }

        pedido.setPago(true);
        pedido.setFormaPagamento(forma);
        pedido.setDataPagamento(LocalDateTime.now());
        
        Pedido salvo = pedidoRepository.save(pedido);
        registrarLog(salvo, "Pagamento efetuado via: " + forma);
        
        return mapToResponse(salvo);
    }

    @Transactional
    public PedidoResponseDTO editarPedido(@NonNull Long id, PedidoRequestDTO request, String motivo) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado."));

        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Não e possivel editar um pedido encerrado ou cancelado.");
        }

        BigDecimal totalAnterior = pedido.getTotal();
        
        estornarEstoque(pedido);
        pedido.getItens().clear();

        List<ItemPedido> novosItens = request.getItens().stream().map(itemDto -> {
            Long produtoId = itemDto.getProdutoId();
            if (produtoId == null) {
                throw new IllegalArgumentException("ID do produto não pode ser nulo.");
            }
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado ID: " + produtoId));

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
        
        pedido.setClienteNome(request.getClienteNome());
        pedido.setClienteTelefone(request.getClienteTelefone());
        pedido.setEnderecoEntrega(request.getEnderecoEntrega());
        pedido.setTaxaEntrega(request.getTaxaEntrega() != null ? request.getTaxaEntrega() : BigDecimal.ZERO);
        pedido.setTaxaServico(request.getTaxaServico() != null ? request.getTaxaServico() : BigDecimal.ZERO);

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
        registrarLog(salvo, logDetalhe);

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

        BigDecimal totalPerdas = movimentacaoService.calcularTotalPerdasNoPeriodo(inicio, fim);

        return RelatorioFaturamentoDTO.builder()
                .faturamentoTotal(faturamentoTotal)
                .faturamentoPorTipo(faturamentoPorTipo)
                .faturamentoPorForma(faturamentoPorForma)
                .pedidosAuditados(pedidosAuditados)
                .itensMaisVendidos(itensRanking)
                .valorTotalPerdas(totalPerdas)
                .lucroEstimado(faturamentoTotal.subtract(totalPerdas))
                .build();
    }

    private void validarEDeduzirEstoque(Produto produto, Integer quantidadePedido) {
        if (produto.getQuantidade() != null && produto.getQuantidade() > 0) {
            if (produto.getQuantidade() < quantidadePedido) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidade(produto.getQuantidade() - quantidadePedido);
            produtoRepository.save(produto);
            movimentacaoService.registrarVendaProduto(produto, quantidadePedido, "Venda direta de produto");
        }

        if (produto.getItensComposicao() != null && !produto.getItensComposicao().isEmpty()) {
            List<Ingrediente> ingredientesParaSalvar = new ArrayList<>();
            for (Composicao composicao : produto.getItensComposicao()) {
                Ingrediente ingrediente = composicao.getIngrediente();
                BigDecimal quantidadeNecessaria = composicao.getQuantidade().multiply(new BigDecimal(quantidadePedido));
                
                if (ingrediente.getQuantidade().compareTo(quantidadeNecessaria) < 0) {
                    throw new RuntimeException("Estoque insuficiente do ingrediente [" + ingrediente.getNome() + 
                                               "] para produzir o prato [" + produto.getNome() + "].");
                }
                
                ingrediente.setQuantidade(ingrediente.getQuantidade().subtract(quantidadeNecessaria));
                ingredientesParaSalvar.add(ingrediente);
                movimentacaoService.registrarVenda(ingrediente, quantidadeNecessaria, "Venda via prato: " + produto.getNome());
            }
            if (!ingredientesParaSalvar.isEmpty()) {
                ingredienteRepository.saveAll(ingredientesParaSalvar);
            }
        }
    }

    private void estornarEstoque(Pedido pedido) {
        List<Produto> produtosParaEstornar = new ArrayList<>();
        List<Ingrediente> ingredientesParaEstornar = new ArrayList<>();

        pedido.getItens().forEach(item -> {
            Produto produto = item.getProduto();
            Integer qtdPedido = item.getQuantidade();

            if (produto.getQuantidade() != null) {
                produto.setQuantidade(produto.getQuantidade() + qtdPedido);
                produtosParaEstornar.add(produto);
                movimentacaoService.registrarVendaProduto(produto, -qtdPedido, "Estorno de pedido ID: " + pedido.getId());
            }

            if (produto.getItensComposicao() != null) {
                for (Composicao composicao : produto.getItensComposicao()) {
                    Ingrediente ingrediente = composicao.getIngrediente();
                    BigDecimal quantidadeParaEstornar = composicao.getQuantidade().multiply(new BigDecimal(qtdPedido));
                    ingrediente.setQuantidade(ingrediente.getQuantidade().add(quantidadeParaEstornar));
                    ingredientesParaEstornar.add(ingrediente);
                    movimentacaoService.registrarVenda(ingrediente, quantidadeParaEstornar.negate(), "Estorno de pedido ID: " + pedido.getId());
                }
            }
        });

        if (!produtosParaEstornar.isEmpty()) {
            produtoRepository.saveAll(produtosParaEstornar);
        }
        if (!ingredientesParaEstornar.isEmpty()) {
            ingredienteRepository.saveAll(ingredientesParaEstornar);
        }
    }

    private void liberarMesa(Pedido pedido) {
        if (pedido.getMesa() != null) {
            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesaRepository.save(mesa);
        }
    }

    private void registrarLog(Pedido pedido, String descricao) {
        PedidoLog log = new PedidoLog();
        log.setPedido(pedido);
        log.setStatus(pedido.getStatus());
        log.setDescricao(descricao);
        log.setUsuario(getUsuarioAutenticado());
        pedidoLogRepository.save(log);
    }

    private PedidoResponseDTO mapToResponse(Pedido p) {
        PedidoResponseDTO.PedidoResponseDTOBuilder builder = PedidoResponseDTO.builder()
                .id(p.getId())
                .identificacao(p.getIdentificacao())
                .dataCriacao(p.getDataCriacao());

        if (p.getMesa() != null) {
            MesaResponseDTO mesaDto = new MesaResponseDTO();
            mesaDto.setId(p.getMesa().getId());
            mesaDto.setNumero(p.getMesa().getNumero());
            mesaDto.setStatus(p.getMesa().getStatus());
            mesaDto.setCapacidade(p.getMesa().getCapacidade());
            mesaDto.setAtiva(p.getMesa().getAtiva());
            builder.mesa(mesaDto);
        }

        return builder
                .status(p.getStatus())
                .tipoPedido(p.getTipoPedido())
                .formaPagamento(p.getFormaPagamento())
                .pago(p.getPago())
                .dataPagamento(p.getDataPagamento())
                .total(p.getTotal())
                .observacao(p.getObservacao())
                .clienteNome(p.getClienteNome())
                .clienteTelefone(p.getClienteTelefone())
                .enderecoEntrega(p.getEnderecoEntrega())
                .taxaEntrega(p.getTaxaEntrega())
                .taxaServico(p.getTaxaServico())
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
