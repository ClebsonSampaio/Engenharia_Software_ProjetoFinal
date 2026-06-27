package com.fazendapro.animal.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "animais")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Animal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nbr;

    @Column(length = 80)
    private String nome;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raca_id")
    private Raca raca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SexoAnimal sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAnimal status = StatusAnimal.ATIVO;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id")
    private Animal pai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id")
    private Animal mae;

    @Column(name = "peso_entrada", precision = 8, scale = 2)
    private BigDecimal pesoEntrada;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
