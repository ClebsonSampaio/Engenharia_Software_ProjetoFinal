package com.fazendapro.financeiro.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldo,
    String periodo
) {}
