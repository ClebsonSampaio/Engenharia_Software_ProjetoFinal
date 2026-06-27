package com.fazendapro.leite.repository;

import com.fazendapro.leite.model.ProducaoLeite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProducaoLeiteRepository extends JpaRepository<ProducaoLeite, Long> {

    List<ProducaoLeite> findByAnimalIdOrderByDataDesc(Long animalId);
    Optional<ProducaoLeite> findByAnimalIdAndData(Long animalId, LocalDate data);
    List<ProducaoLeite> findByDataBetweenOrderByDataDesc(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(p.quantidadeTotal) FROM ProducaoLeite p WHERE p.data BETWEEN :inicio AND :fim")
    BigDecimal somarProducaoPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT SUM(p.quantidadeTotal) FROM ProducaoLeite p WHERE p.animal.id = :animalId AND p.data BETWEEN :inicio AND :fim")
    BigDecimal somarProducaoPorAnimalEPeriodo(@Param("animalId") Long animalId,
                                              @Param("inicio") LocalDate inicio,
                                              @Param("fim") LocalDate fim);
}
