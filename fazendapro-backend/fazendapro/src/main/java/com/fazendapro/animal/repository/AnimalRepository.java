package com.fazendapro.animal.repository;

import com.fazendapro.animal.model.Animal;
import com.fazendapro.animal.model.StatusAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findByNbr(String nbr);
    boolean existsByNbr(String nbr);
    List<Animal> findByStatus(StatusAnimal status);
    List<Animal> findByMaeId(Long maeId);
    List<Animal> findByPaiId(Long paiId);

    @Query("SELECT a FROM Animal a WHERE a.status = 'ATIVO' AND a.sexo = 'FEMEA'")
    List<Animal> findFemeasAtivas();

    @Query("SELECT a FROM Animal a WHERE " +
           "LOWER(a.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(a.nbr)  LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Animal> buscar(@Param("termo") String termo);
}
