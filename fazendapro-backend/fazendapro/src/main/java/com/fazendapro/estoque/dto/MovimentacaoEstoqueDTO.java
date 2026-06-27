package com.fazendapro.estoque.dto;

import com.fazendapro.estoque.model.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoEstoqueDTO(
    Long id, @NotNull Long produtoId, String produtoNome,
    @NotNull TipoMovimentacao tipo, @NotNull @Positive BigDecimal quantidade,
    BigDecimal custoUnitario, BigDecimal custoTotal,
    @NotNull LocalDate dataMovimentacao,
    Long animalId, String motivo, String numeroNota
) {}
