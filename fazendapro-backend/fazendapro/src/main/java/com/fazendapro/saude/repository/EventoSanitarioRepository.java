package com.fazendapro.saude.repository;

import com.fazendapro.saude.model.EventoSanitario;
import com.fazendapro.saude.model.TipoEventoSanitario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface EventoSanitarioRepository extends JpaRepository<EventoSanitario, Long> {
    List<EventoSanitario> findByAnimalIdOrderByDataEventoDesc(Long animalId);
    List<EventoSanitario> findByTipoAndDataEventoBetween(TipoEventoSanitario tipo, LocalDate inicio, LocalDate fim);

    @Query("SELECT e FROM EventoSanitario e WHERE e.dataFimCarencia >= CURRENT_DATE ORDER BY e.dataFimCarencia")
    List<EventoSanitario> findAnimaisEmCarencia();
}
