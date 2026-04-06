package com.estoque.sistema.controller;

import com.estoque.sistema.dto.*;
import com.estoque.sistema.model.FormaPagamento;
import com.estoque.sistema.model.StatusPedido;
import com.estoque.sistema.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestão do ciclo de vida dos pedidos e estoque de ingredientes")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Criar novo pedido")
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody PedidoRequestDTO request) {
        return ResponseEntity.ok(pedidoService.criarPedido(request));
    }

    @Operation(summary = "Listar fila de pedidos ativos")
    @GetMapping("/fila")
    public List<PedidoResponseDTO> listarFila() {
        return pedidoService.listarFilaAtiva();
    }

    @Operation(summary = "Atualizar status do pedido")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable @NonNull Long id, 
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status, motivo));
    }

    @Operation(summary = "Registrar pagamento")
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<PedidoResponseDTO> pagarPedido(
            @PathVariable @NonNull Long id, 
            @RequestParam FormaPagamento forma) {
        return ResponseEntity.ok(pedidoService.pagarPedido(id, forma));
    }

    @Operation(summary = "Editar pedido existente")
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> editarPedido(
            @PathVariable @NonNull Long id, 
            @RequestBody PedidoRequestDTO request,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.editarPedido(id, request, motivo));
    }

    @Operation(summary = "Relatório mensal de faturamento")
    @GetMapping("/relatorios/mensal")
    public ResponseEntity<RelatorioFaturamentoDTO> relatorioMensal(@RequestParam int mes, @RequestParam int ano) {
        return ResponseEntity.ok(pedidoService.gerarFechamentoMensal(mes, ano));
    }
}
