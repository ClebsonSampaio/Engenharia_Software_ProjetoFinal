package com.fazendapro.financeiro.dto;

import com.fazendapro.financeiro.model.CategoriaFinanceiro;
import com.fazendapro.financeiro.model.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoFinanceiroDTO(
    Long id,
    @NotNull TipoLancamento tipo,
    @NotNull CategoriaFinanceiro categoria,
    @NotBlank String descricao,
    @NotNull @Positive BigDecimal valor,
    @NotNull LocalDate dataLancamento,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    boolean pago,
    Long animalId,
    String observacoes
) {}
