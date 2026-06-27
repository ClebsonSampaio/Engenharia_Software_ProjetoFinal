package com.fazendapro.animal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "racas")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Raca {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;
}
