package com.fazendapro.saude.service;

import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.exception.ResourceNotFoundException;
import com.fazendapro.saude.dto.EventoSanitarioDTO;
import com.fazendapro.saude.model.EventoSanitario;
import com.fazendapro.saude.repository.EventoSanitarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventoSanitarioService {

    private final EventoSanitarioRepository eventoRepo;
    private final AnimalRepository animalRepo;

    public List<EventoSanitarioDTO> listarPorAnimal(Long animalId) {
        return eventoRepo.findByAnimalIdOrderByDataEventoDesc(animalId).stream().map(this::toDTO).toList();
    }

    public List<EventoSanitarioDTO> listarEmCarencia() {
        return eventoRepo.findAnimaisEmCarencia().stream().map(this::toDTO).toList();
    }

    @Transactional
    public EventoSanitarioDTO registrar(EventoSanitarioDTO dto) {
        var animal = animalRepo.findById(dto.animalId())
            .orElseThrow(() -> new ResourceNotFoundException("Animal", dto.animalId()));
        var evento = EventoSanitario.builder()
            .animal(animal).tipo(dto.tipo()).descricao(dto.descricao())
            .dataEvento(dto.dataEvento()).dataRetorno(dto.dataRetorno())
            .carenciaDias(dto.carenciaDias() != null ? dto.carenciaDias() : 0)
            .veterinario(dto.veterinario()).produtoAplicado(dto.produtoAplicado())
            .dose(dto.dose()).custo(dto.custo() != null ? dto.custo() : BigDecimal.ZERO)
            .observacoes(dto.observacoes())
            .build();
        return toDTO(eventoRepo.save(evento));
    }

    @Transactional
    public void excluir(Long id) {
        if (!eventoRepo.existsById(id)) throw new ResourceNotFoundException("Evento sanitário", id);
        eventoRepo.deleteById(id);
    }

    private EventoSanitarioDTO toDTO(EventoSanitario e) {
        return new EventoSanitarioDTO(e.getId(), e.getAnimal().getId(), e.getAnimal().getNbr(),
            e.getTipo(), e.getDescricao(), e.getDataEvento(), e.getDataRetorno(),
            e.getCarenciaDias(), e.getDataFimCarencia(), e.getVeterinario(),
            e.getProdutoAplicado(), e.getDose(), e.getCusto(), e.getObservacoes());
    }
}
