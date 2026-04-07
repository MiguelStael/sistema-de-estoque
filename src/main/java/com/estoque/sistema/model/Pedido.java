package com.estoque.sistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@SQLDelete(sql = "UPDATE pedidos SET ativo = false WHERE id = ? AND version = ?")
@SQLRestriction("ativo = true")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false, length = 100)
    private String identificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Column(nullable = false)
    private Boolean pago = false;

    private LocalDateTime dataPagamento;

    @Column(name = "cliente_nome", length = 150)
    private String clienteNome;

    @Column(name = "cliente_telefone", length = 20)
    private String clienteTelefone;

    @Column(name = "endereco_entrega", length = 500)
    private String enderecoEntrega;

    @Column(name = "taxa_entrega", precision = 10, scale = 2)
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    @Column(name = "taxa_servico", precision = 10, scale = 2)
    private BigDecimal taxaServico = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoLog> logs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public void calcularTotal() {
        BigDecimal totalItens = BigDecimal.ZERO;
        if (itens != null) {
            totalItens = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        BigDecimal tEntrega = taxaEntrega != null ? taxaEntrega : BigDecimal.ZERO;
        BigDecimal tServico = taxaServico != null ? taxaServico : BigDecimal.ZERO;
        
        this.total = totalItens.add(tEntrega).add(tServico);
    }
}
