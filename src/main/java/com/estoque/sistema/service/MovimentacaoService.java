package com.estoque.sistema.service;

import com.estoque.sistema.model.Ingrediente;
import com.estoque.sistema.model.Movimentacao;
import com.estoque.sistema.model.Produto;
import com.estoque.sistema.model.TipoMovimentacao;
import com.estoque.sistema.repository.MovimentacaoRepository;
import com.estoque.sistema.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.estoque.sistema.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElse(null);
    }

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
        mov.setUsuario(getUsuarioAutenticado());
        movimentacaoRepository.save(mov);

        if (tipo.name().startsWith("SAIDA")) {
            verificarAlertaCritico(ingrediente, produto);
        }
    }

    private void verificarAlertaCritico(Ingrediente ingrediente, Produto produto) {
        if (ingrediente != null && ingrediente.getQuantidade().compareTo(ingrediente.getQuantidadeMinima()) <= 0) {
            String html = String.format(
                "<h2 style='color: red;'>ALERTA DE ESTOQUE CRÍTICO</h2>" +
                "<p>O item <b>%s</b> atingiu o nível crítico após uma movimentação de saída.</p>" +
                "<p><b>Quantidade Atual:</b> %s %s</p>" +
                "<p><b>Quantidade Mínima:</b> %s</p>",
                ingrediente.getNome(), ingrediente.getQuantidade(), ingrediente.getUnidadeMedida(), ingrediente.getQuantidadeMinima()
            );
            emailService.enviarAlertaEstoqueCritico(html);
        }
        
        if (produto != null && new BigDecimal(produto.getQuantidade()).compareTo(new BigDecimal(produto.getQuantidadeMinima())) <= 0) {
            String html = String.format(
                "<h2 style='color: red;'>ALERTA DE PRODUTO BAIXO</h2>" +
                "<p>O produto <b>%s</b> está com poucas unidades disponíveis.</p>" +
                "<p><b>Quantidade Atual:</b> %s</p>",
                produto.getNome(), produto.getQuantidade()
            );
            emailService.enviarAlertaEstoqueCritico(html);
        }
    }

    public BigDecimal calcularTotalPerdasNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal total = movimentacaoRepository.sumTotalPerdas(TipoMovimentacao.SAIDA_PERDA, inicio, fim);
        return total != null ? total : BigDecimal.ZERO;
    }
}
