package com.estoque.sistema.service;

import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.model.Movimentacao;
import com.estoque.sistema.model.Produto;
import com.estoque.sistema.model.TipoMovimentacao;
import com.estoque.sistema.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public void registrarVenda(Ingrediente ingrediente, BigDecimal quantidade, String motivo) {
        registrar(ingrediente, null, quantidade, TipoMovimentacao.SAIDA_VENDA, motivo, ingrediente.getCustoUnitario());
    }

    @Transactional
    public void registrarVendaProduto(Produto produto, Integer quantidade, String motivo) {
        registrar(null, produto, new BigDecimal(quantidade), TipoMovimentacao.SAIDA_VENDA, motivo, produto.getPreco());
    }

    @Transactional
    public void registrarPerda(Ingrediente ingrediente, BigDecimal quantidade, String motivo) {
        registrar(ingrediente, null, quantidade, TipoMovimentacao.SAIDA_PERDA, motivo, ingrediente.getCustoUnitario());
    }

    @Transactional
    public void registrarAjuste(Ingrediente ingrediente, BigDecimal quantidade, String motivo) {
        registrar(ingrediente, null, quantidade, TipoMovimentacao.AJUSTE, motivo, ingrediente.getCustoUnitario());
    }

    private void registrar(Ingrediente ingrediente, Produto produto, 
                           BigDecimal quantidade, TipoMovimentacao tipo, String motivo, BigDecimal valorCusto) {
        Movimentacao mov = new Movimentacao();
        mov.setIngrediente(ingrediente);
        mov.setProduto(produto);
        mov.setQuantidade(quantidade);
        mov.setTipo(tipo);
        mov.setMotivo(motivo);
        mov.setValorUnitarioCusto(valorCusto);
        movimentacaoRepository.save(mov);
    }

    public BigDecimal calcularTotalPerdasNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal total = movimentacaoRepository.sumTotalPerdas(TipoMovimentacao.SAIDA_PERDA, inicio, fim);
        return total != null ? total : BigDecimal.ZERO;
    }
}
