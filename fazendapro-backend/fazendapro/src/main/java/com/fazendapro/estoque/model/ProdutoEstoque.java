package com.fazendapro.estoque.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos_estoque")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProdutoEstoque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaEstoque categoria;

    @Column(nullable = false, length = 20)
    private String unidade;

    @Column(name = "quantidade_atual", precision = 12, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;

    @Column(name = "quantidade_minima", precision = 12, scale = 3)
    private BigDecimal quantidadeMinima = BigDecimal.ZERO;

    @Column(name = "custo_medio", precision = 10, scale = 4)
    private BigDecimal custoMedio = BigDecimal.ZERO;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(length = 100)
    private String fabricante;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() { criadoEm = atualizadoEm = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { atualizadoEm = LocalDateTime.now(); }

    public boolean estaBaixoEstoqueMinimo() {
        return quantidadeAtual.compareTo(quantidadeMinima) < 0;
    }
}
