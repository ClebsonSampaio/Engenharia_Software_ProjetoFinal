package com.fazendapro.estoque.model;

import com.fazendapro.animal.model.Animal;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MovimentacaoEstoque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEstoque produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacao tipo;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "custo_unitario", precision = 10, scale = 4)
    private BigDecimal custoUnitario;

    @Column(name = "custo_total", precision = 12, scale = 4)
    private BigDecimal custoTotal;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDate dataMovimentacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Column(length = 200)
    private String motivo;

    @Column(name = "numero_nota", length = 50)
    private String numeroNota;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() { criadoEm = LocalDateTime.now(); }
}
