package com.estoque.sistema.config;

import com.estoque.sistema.dto.AlertaEstoqueDTO;
import com.estoque.sistema.service.EmailService;
import com.estoque.sistema.service.MonitoramentoEstoqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgendadorAlertasEstoque {

    private final MonitoramentoEstoqueService monitoramentoEstoqueService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarEstoquesDiario() {
        log.info("Iniciando verificação diária de estoque...");
        processarAlertas();
    }

    public void processarAlertas() {
        List<AlertaEstoqueDTO> alertas = monitoramentoEstoqueService.listarAlertasCriticos();

        if (!alertas.isEmpty()) {
            String htmlData = construirTemplateHtml(alertas);
            emailService.enviarAlertaEstoqueCritico(htmlData);
            log.info("Alertas de estoque detectados e e-mail enviado.");
        } else {
            log.info("Nenhum item em estado de alerta ou crítico detectado.");
        }
    }

    private String construirTemplateHtml(List<AlertaEstoqueDTO> alertas) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>Relatorio de Status de Estoque</h2>");
        html.append("<p>O sistema identificou itens que precisam de atencao:</p>");
        html.append("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
        html.append(
                "<tr style='background-color: #f2f2f2;'><th>Item</th><th>Tipo</th><th>Qtd Atual</th><th>Qtd Minima</th><th>Status</th></tr>");

        for (AlertaEstoqueDTO item : alertas) {
            boolean isCritico = "CRITICO".equals(item.getStatus());
            String color = isCritico ? "#d9534f" : "#f0ad4e";

            html.append("<tr>");
            html.append("<td>").append(item.getNome()).append("</td>");
            html.append("<td>").append(item.getTipo()).append("</td>");
            html.append("<td>").append(item.getQuantidadeAtual()).append(" ").append(item.getUnidadeMedida())
                    .append("</td>");
            html.append("<td>").append(item.getQuantidadeMinima()).append("</td>");
            html.append("<td style='color: ").append(color).append("; font-weight: bold;'>")
                    .append(item.getStatus())
                    .append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append(
                "<p style='margin-top: 20px;'>Este e um e-mail automatico enviado pelo seu <strong>Sistema de Estoque Inteligente</strong>.</p>");
        html.append("</body></html>");
        return html.toString();
    }
}
