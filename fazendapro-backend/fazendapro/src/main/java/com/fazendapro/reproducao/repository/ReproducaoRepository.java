package com.fazendapro.reproducao.repository;

import com.fazendapro.reproducao.model.Reproducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ReproducaoRepository extends JpaRepository<Reproducao, Long> {
    List<Reproducao> findByFemeaIdOrderByDataCoberturaDesc(Long femeaId);

    @Query("SELECT r FROM Reproducao r WHERE r.dataDpp BETWEEN :inicio AND :fim ORDER BY r.dataDpp")
    List<Reproducao> findPartosPrevistos(LocalDate inicio, LocalDate fim);

    @Query("SELECT r FROM Reproducao r WHERE r.diagnosticoGestacao = 'PENDENTE' ORDER BY r.dataCobertura")
    List<Reproducao> findDiagnosticoPendente();
}
