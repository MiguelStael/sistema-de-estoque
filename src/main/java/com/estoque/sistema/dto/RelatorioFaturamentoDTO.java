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
    private Map<String, BigDecimal> faturamentoPorTipo;
    private Map<String, BigDecimal> faturamentoPorForma;
    private org.springframework.data.domain.Page<PedidoResponseDTO> pedidosAuditados;
    private List<Map<String, Object>> itensMaisVendidos;
    private BigDecimal valorTotalPerdas;
    private BigDecimal lucroEstimado;
}
