package com.estoque.sistema.service;

import com.estoque.sistema.dto.AlertaEstoqueDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoramentoAgendadoService {

    private final MonitoramentoEstoqueService monitoramentoService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void enviarResumoDiario() {
        log.info("Iniciando verificação agendada de estoque...");
        
        List<AlertaEstoqueDTO> alertasCriticos = monitoramentoService.listarAlertasCriticos();
        List<AlertaEstoqueDTO> alertasVencimento = monitoramentoService.listarAlertasVencimento();

        if (alertasCriticos.isEmpty() && alertasVencimento.isEmpty()) {
            log.info("Estoque saudável. Nenhum alerta pendente.");
            return;
        }

        String html = construirCorpoEmail(alertasCriticos, alertasVencimento);
        emailService.enviarAlertaEstoqueCritico(html);
    }

    private String construirCorpoEmail(List<AlertaEstoqueDTO> criticos, List<AlertaEstoqueDTO> vencimentos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1 style='color: #d32f2f;'>Relatório Proativo de Estoque</h1>");
        sb.append("<p>Prezado Administrador, segue o resumo dos itens que requerem atenção hoje.</p>");

        if (!criticos.isEmpty()) {
            sb.append("<h2>Itens em Nível Crítico ou Baixo</h2>");
            sb.append("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
            sb.append("<tr style='background-color: #f2f2f2;'><th>Item</th><th>Qtd Atual</th><th>Qtd Mínima</th><th>Status</th></tr>");
            for (AlertaEstoqueDTO a : criticos) {
                String cor = "CRITICO".equals(a.getStatus()) ? "red" : "orange";
                sb.append("<tr>")
                  .append("<td>").append(a.getNome()).append("</td>")
                  .append("<td>").append(a.getQuantidadeAtual()).append(" ").append(a.getUnidadeMedida()).append("</td>")
                  .append("<td>").append(a.getQuantidadeMinima()).append("</td>")
                  .append("<td style='color: ").append(cor).append("; font-weight: bold;'>").append(a.getStatus()).append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }

        if (!vencimentos.isEmpty()) {
            sb.append("<h2>Alertas de Vencimento (Próximos 7 dias)</h2>");
            sb.append("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
            sb.append("<tr style='background-color: #f2f2f2;'><th>Item</th><th>Data de Validade</th><th>Status</th></tr>");
            for (AlertaEstoqueDTO v : vencimentos) {
                sb.append("<tr>")
                  .append("<td>").append(v.getNome()).append("</td>")
                  .append("<td>").append(v.getDataValidade()).append("</td>")
                  .append("<td style='color: red; font-weight: bold;'>VENCENDO</td>")
                  .append("</tr>");
            }
            sb.append("</table>");
        }

        sb.append("<br><p><i>Este é um e-mail automático enviado pelo sistema CoreEstoque.</i></p>");
        return sb.toString();
    }
}
