package com.estoque.sistema.service;

import com.estoque.sistema.dto.RelatorioFaturamentoDTO;
import com.estoque.sistema.dto.PedidoResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class RelatorioExcelService {

    public byte[] gerarExcelFaturamento(RelatorioFaturamentoDTO relatorio, int mes, int ano) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Folha 1: Resumo Geral
            Sheet sheetResumo = workbook.createSheet("Resumo Faturamento");
            
            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            int rowNum = 0;
            
            // Titulo
            Row titleRow = sheetResumo.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Relatório de Faturamento Mensal - " + mes + "/" + ano);
            titleCell.setCellStyle(headerStyle);
            
            rowNum++; // Espaço

            // Resumo Financeiro
            createRow(sheetResumo, rowNum++, "Faturamento Total", relatorio.getFaturamentoTotal(), currencyStyle);
            createRow(sheetResumo, rowNum++, "Valor Total de Perdas", relatorio.getValorTotalPerdas(), currencyStyle);
            createRow(sheetResumo, rowNum++, "Lucro Estimado", relatorio.getLucroEstimado(), currencyStyle);

            rowNum += 2; // Espaço

            // Faturamento por Tipo de Pedido
            createSectionHeader(sheetResumo, rowNum++, "Faturamento por Tipo de Pedido", headerStyle);
            for (Map.Entry<String, java.math.BigDecimal> entry : relatorio.getFaturamentoPorTipo().entrySet()) {
                createRow(sheetResumo, rowNum++, entry.getKey(), entry.getValue(), currencyStyle);
            }

            rowNum += 2; // Espaço

            // Itens Mais Vendidos
            createSectionHeader(sheetResumo, rowNum++, "Itens Mais Vendidos", headerStyle);
            Row headerItens = sheetResumo.createRow(rowNum++);
            headerItens.createCell(0).setCellValue("Produto");
            headerItens.createCell(1).setCellValue("Quantidade");
            
            for (Map<String, Object> item : relatorio.getItensMaisVendidos()) {
                Row row = sheetResumo.createRow(rowNum++);
                row.createCell(0).setCellValue(item.get("produto").toString());
                row.createCell(1).setCellValue(Double.parseDouble(item.get("quantidade").toString()));
            }

            // Folha 2: Detalhamento de Pedidos
            Sheet sheetPedidos = workbook.createSheet("Detalhamento Pedidos");
            int rowNumPedidos = 0;
            Row headerRowPedidos = sheetPedidos.createRow(rowNumPedidos++);
            String[] colunas = {"ID", "Identificação", "Status", "Tipo", "Total", "Data"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRowPedidos.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            for (PedidoResponseDTO pedido : relatorio.getPedidosAuditados().getContent()) {
                Row row = sheetPedidos.createRow(rowNumPedidos++);
                row.createCell(0).setCellValue(pedido.getId());
                row.createCell(1).setCellValue(pedido.getIdentificacao());
                row.createCell(2).setCellValue(pedido.getStatus().name());
                row.createCell(3).setCellValue(pedido.getTipoPedido().name());
                Cell cellTotal = row.createCell(4);
                cellTotal.setCellValue(pedido.getTotal().doubleValue());
                cellTotal.setCellStyle(currencyStyle);
                row.createCell(5).setCellValue(pedido.getDataCriacao().toString());
            }

            // Auto-size colunas
            for (int i = 0; i < 10; i++) {
                sheetResumo.autoSizeColumn(i);
                sheetPedidos.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("R$ #,##0.00"));
        return style;
    }

    private void createRow(Sheet sheet, int rowNum, String label, java.math.BigDecimal value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        Cell cell = row.createCell(1);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(style);
        } else {
            cell.setCellValue(0.0);
        }
    }

    private void createSectionHeader(Sheet sheet, int rowNum, String title, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
    }
}
