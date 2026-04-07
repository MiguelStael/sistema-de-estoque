package com.estoque.sistema.controller;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.StatusPedido;
import com.estoque.sistema.service.PedidoService;
import com.estoque.sistema.service.RelatorioExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final RelatorioExcelService relatorioExcelService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO request) {
        return ResponseEntity.ok(pedidoService.criarPedido(request));
    }

    @GetMapping("/fila")
    public List<PedidoResponseDTO> listarFila() {
        return pedidoService.listarFilaAtiva();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable @NonNull Long id, 
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status, motivo));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<PedidoResponseDTO> pagarPedido(
            @PathVariable @NonNull Long id, 
            @RequestParam FormaPagamento forma) {
        return ResponseEntity.ok(pedidoService.pagarPedido(id, forma));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> editarPedido(
            @PathVariable @NonNull Long id, 
            @RequestBody PedidoRequestDTO request,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.editarPedido(id, request, motivo));
    }

    @GetMapping("/relatorios/mensal")
    public ResponseEntity<RelatorioFaturamentoDTO> relatorioMensal(@RequestParam int mes, @RequestParam int ano) {
        return ResponseEntity.ok(pedidoService.gerarFechamentoMensal(mes, ano));
    }

    @GetMapping("/relatorios/mensal/exportar")
    public ResponseEntity<byte[]> exportarRelatorioMensal(@RequestParam int mes, @RequestParam int ano) throws java.io.IOException {
        RelatorioFaturamentoDTO relatorio = pedidoService.gerarFechamentoMensal(mes, ano);
        byte[] excelContent = relatorioExcelService.gerarExcelFaturamento(relatorio, mes, ano);

        String fileName = String.format("faturamento-%02d-%d.xlsx", mes, ano);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
}
