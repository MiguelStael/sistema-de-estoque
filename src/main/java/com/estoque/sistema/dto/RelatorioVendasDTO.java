package com.estoque.sistema.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class RelatorioVendasDTO {
    private BigDecimal faturamentoTotal;
    private List<Map<String, Object>> itensMaisVendidos;
}
