package com.estoque.sistema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos")
@SQLDelete(sql = "UPDATE produtos SET ativo = false WHERE id = ? AND version = ?")
@SQLRestriction("ativo = true")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Composicao> itensComposicao = new ArrayList<>();

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "url_imagem")
    private String urlImagem;

    @Column(name = "quantidade_minima")
    private Integer quantidadeMinima;

    @Column(nullable = false)
    private Boolean disponivel = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
