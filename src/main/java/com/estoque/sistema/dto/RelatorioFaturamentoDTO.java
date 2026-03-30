package com.estoque.sistema.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RelatorioFaturamentoDTO {
    private BigDecimal faturamentoTotal;
    private Map<String, BigDecimal> faturamentoPorTipo; // Presencial vs Delivery
    private Map<String, BigDecimal> faturamentoPorForma; // Pix, Cartão, etc
    private List<PedidoResponseDTO> pedidosAuditados; // Lista para conferência
    private List<Map<String, Object>> itensMaisVendidos;
}
