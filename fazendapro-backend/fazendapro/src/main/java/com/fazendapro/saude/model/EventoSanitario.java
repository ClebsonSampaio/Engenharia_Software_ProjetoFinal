package com.fazendapro.saude.model;

import com.fazendapro.animal.model.Animal;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_sanitarios")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class EventoSanitario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEventoSanitario tipo;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;

    @Column(name = "carencia_dias")
    private Integer carenciaDias = 0;

    @Column(name = "data_fim_carencia")
    private LocalDate dataFimCarencia;

    @Column(length = 100)
    private String veterinario;

    @Column(name = "produto_aplicado", length = 100)
    private String produtoAplicado;

    @Column(length = 50)
    private String dose;

    @Column(precision = 10, scale = 2)
    private BigDecimal custo = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (carenciaDias != null && carenciaDias > 0 && dataEvento != null) {
            dataFimCarencia = dataEvento.plusDays(carenciaDias);
        }
    }
}
