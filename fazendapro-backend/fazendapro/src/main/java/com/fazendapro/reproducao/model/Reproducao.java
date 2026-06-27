package com.fazendapro.reproducao.model;

import com.fazendapro.animal.model.Animal;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reproducoes")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Reproducao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "femea_id", nullable = false)
    private Animal femea;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobertura", nullable = false, length = 20)
    private TipoCobertura tipoCobertura;

    @Column(name = "data_cobertura", nullable = false)
    private LocalDate dataCobertura;

    // Data Provável do Parto (dataCobertura + 283 dias)
    @Column(name = "data_dpp")
    private LocalDate dataDpp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "touro_id")
    private Animal touro;

    @Column(name = "nome_touro_externo", length = 100)
    private String nomeTourouExterno;

    @Column(name = "semen_codigo", length = 50)
    private String semenCodigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "diagnostico_gestacao", length = 20)
    private DiagnosticoGestacao diagnosticoGestacao = DiagnosticoGestacao.PENDENTE;

    @Column(name = "data_diagnostico")
    private LocalDate dataDiagnostico;

    @Column(name = "data_parto")
    private LocalDate dataParto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cria_id")
    private Animal cria;

    @Column(length = 20)
    private String resultado;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        // DPP = data cobertura + 283 dias (gestação bovina)
        if (dataCobertura != null && dataDpp == null) {
            dataDpp = dataCobertura.plusDays(283);
        }
    }
}
