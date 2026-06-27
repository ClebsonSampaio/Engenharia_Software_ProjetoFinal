package com.fazendapro.leite.dto;

import com.fazendapro.leite.model.ClassificacaoLeite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProducaoLeiteDTO(
    Long id,
    @NotNull(message = "Animal é obrigatório") Long animalId,
    String animalNbr,
    String animalNome,
    @NotNull(message = "Data é obrigatória") LocalDate data,
    @PositiveOrZero BigDecimal quantidadeManha,
    @PositiveOrZero BigDecimal quantidadeTarde,
    BigDecimal quantidadeTotal,
    ClassificacaoLeite classificacao,
    Integer ccs,
    BigDecimal cbt,
    String observacoes
) {}
