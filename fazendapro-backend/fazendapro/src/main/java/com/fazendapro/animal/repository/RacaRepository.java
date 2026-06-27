package com.fazendapro.animal.repository;

import com.fazendapro.animal.model.Raca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RacaRepository extends JpaRepository<Raca, Long> {
    List<Raca> findByAtivoTrue();
}
