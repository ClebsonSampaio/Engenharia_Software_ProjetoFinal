package com.fazendapro.estoque.dto;

import com.fazendapro.estoque.model.CategoriaEstoque;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoEstoqueDTO(
    Long id, @NotBlank String nome, @NotNull CategoriaEstoque categoria,
    @NotBlank String unidade, BigDecimal quantidadeAtual, BigDecimal quantidadeMinima,
    BigDecimal custoMedio, LocalDate dataValidade, String fabricante,
    boolean ativo, boolean abaixoMinimo
) {}
