package com.fazendapro.financeiro.repository;

import com.fazendapro.financeiro.model.LancamentoFinanceiro;
import com.fazendapro.financeiro.model.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {

    List<LancamentoFinanceiro> findByDataLancamentoBetweenOrderByDataLancamentoDesc(
        LocalDate inicio, LocalDate fim);

    List<LancamentoFinanceiro> findByPagoFalseOrderByDataVencimento();

    @Query("SELECT SUM(l.valor) FROM LancamentoFinanceiro l WHERE l.tipo = :tipo AND l.dataLancamento BETWEEN :inicio AND :fim")
    BigDecimal somarPorTipoEPeriodo(@Param("tipo") TipoLancamento tipo,
                                    @Param("inicio") LocalDate inicio,
                                    @Param("fim") LocalDate fim);
}
