package com.estoque.sistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "ingredientes", indexes = {
    @Index(name = "IndiceEstoqueIngrediente", columnList = "quantidade"),
    @Index(name = "IndiceValidadeIngrediente", columnList = "data_validade")
})
@SQLDelete(sql = "UPDATE ingredientes SET ativo = false WHERE id = ? AND version = ?")
@SQLRestriction("ativo = true")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 10)
    private UnidadeMedida unidadeMedida;

    @Column(name = "custo_unitario", precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "quantidade_minima", precision = 10, scale = 3)
    private BigDecimal quantidadeMinima;

    @Column(name = "data_validade")
    private java.time.LocalDate dataValidade;
}
