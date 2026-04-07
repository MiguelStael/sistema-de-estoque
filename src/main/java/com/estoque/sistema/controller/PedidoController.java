package com.estoque.sistema.controller;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.StatusPedido;
import com.estoque.sistema.service.PedidoService;
import com.estoque.sistema.service.RelatorioExcelService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos presenciais e delivery")
public class PedidoController {

    private final PedidoService pedidoService;
    private final RelatorioExcelService relatorioExcelService;

    @PostMapping
    @Operation(summary = "Criar novo pedido", description = "Cria um pedido presencial ou delivery com validação de taxas.")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody @Valid PedidoRequestDTO request) {
        return ResponseEntity.ok(pedidoService.criarPedido(request));
    }

    @GetMapping("/fila")
    @Operation(summary = "Listar fila de produção", description = "Retorna todos os pedidos pendentes ou em preparo.")
    public List<PedidoResponseDTO> listarFila() {
        return pedidoService.listarFilaAtiva();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Altera o status (ex: EM_PREPARO, ENTREGUE) com trilha de auditoria.")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable @NonNull Long id, 
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status, motivo));
    }

    @PatchMapping("/{id}/pagar")
    @Operation(summary = "Registrar pagamento", description = "Finaliza o pedido definindo a forma de pagamento e liberando a mesa se for presencial.")
    public ResponseEntity<PedidoResponseDTO> pagarPedido(
            @PathVariable @NonNull Long id, 
            @RequestParam @NonNull FormaPagamento forma) {
        return ResponseEntity.ok(pedidoService.pagarPedido(id, java.util.Objects.requireNonNull(forma)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar pedido existente", description = "Permite alterar itens ou dados do cliente antes da finalização.")
    public ResponseEntity<PedidoResponseDTO> editarPedido(
            @PathVariable @NonNull Long id, 
            @RequestBody @Valid PedidoRequestDTO request,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.editarPedido(id, request, motivo));
    }

    @GetMapping("/relatorios/mensal")
    @Operation(summary = "Gerar faturamento mensal", description = "Calcula o total de vendas, taxas e impostos de um mês específico.")
    public ResponseEntity<RelatorioFaturamentoDTO> relatorioMensal(@RequestParam int mes, @RequestParam int ano) {
        return ResponseEntity.ok(pedidoService.gerarFechamentoMensal(mes, ano));
    }

    @GetMapping("/relatorios/mensal/exportar")
    @Operation(summary = "Exportar faturamento para Excel", description = "Gera um arquivo .xlsx profissional com o detalhamento das vendas.")
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
