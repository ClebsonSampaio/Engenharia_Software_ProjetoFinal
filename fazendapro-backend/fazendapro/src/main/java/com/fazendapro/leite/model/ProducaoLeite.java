package com.fazendapro.leite.model;

import com.fazendapro.animal.model.Animal;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "producoes_leite", uniqueConstraints = @UniqueConstraint(columnNames = {"animal_id","data"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProducaoLeite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "quantidade_manha", precision = 8, scale = 2)
    private BigDecimal quantidadeManha = BigDecimal.ZERO;

    @Column(name = "quantidade_tarde", precision = 8, scale = 2)
    private BigDecimal quantidadeTarde = BigDecimal.ZERO;

    @Column(name = "quantidade_total", precision = 8, scale = 2)
    private BigDecimal quantidadeTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClassificacaoLeite classificacao = ClassificacaoLeite.NORMAL;

    private Integer ccs;

    @Column(precision = 12, scale = 2)
    private BigDecimal cbt;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        quantidadeTotal = quantidadeManha.add(quantidadeTarde);
    }

    @PreUpdate
    protected void onUpdate() {
        quantidadeTotal = quantidadeManha.add(quantidadeTarde);
    }
}
