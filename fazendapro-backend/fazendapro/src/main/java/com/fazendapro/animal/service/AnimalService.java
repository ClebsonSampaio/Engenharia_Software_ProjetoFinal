package com.fazendapro.animal.service;

import com.fazendapro.animal.dto.AnimalDTO;
import com.fazendapro.animal.dto.RacaDTO;
import com.fazendapro.animal.model.Animal;
import com.fazendapro.animal.model.Raca;
import com.fazendapro.animal.model.StatusAnimal;
import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.animal.repository.RacaRepository;
import com.fazendapro.exception.BusinessException;
import com.fazendapro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final RacaRepository racaRepository;

    public List<AnimalDTO> listarTodos() {
        return animalRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<AnimalDTO> listarAtivos() {
        return animalRepository.findByStatus(StatusAnimal.ATIVO).stream().map(this::toDTO).toList();
    }

    public AnimalDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    public List<AnimalDTO> buscar(String termo) {
        return animalRepository.buscar(termo).stream().map(this::toDTO).toList();
    }

    // ─── NOVO: filhos do animal (filhotes como pai ou mãe) ───
    public List<AnimalDTO> listarFilhos(Long id) {
        Set<Long> vistos = new HashSet<>();
        List<AnimalDTO> filhos = new ArrayList<>();
        for (Animal a : animalRepository.findByMaeId(id)) {
            if (vistos.add(a.getId())) filhos.add(toDTO(a));
        }
        for (Animal a : animalRepository.findByPaiId(id)) {
            if (vistos.add(a.getId())) filhos.add(toDTO(a));
        }
        return filhos;
    }

    @Transactional
    public AnimalDTO cadastrar(AnimalDTO dto) {
        if (animalRepository.existsByNbr(dto.nbr())) {
            throw new BusinessException("NBR já cadastrado: " + dto.nbr());
        }
        return toDTO(animalRepository.save(toEntity(dto, new Animal())));
    }

    @Transactional
    public AnimalDTO atualizar(Long id, AnimalDTO dto) {
        var animal = findById(id);
        if (!animal.getNbr().equals(dto.nbr()) && animalRepository.existsByNbr(dto.nbr())) {
            throw new BusinessException("NBR já está em uso: " + dto.nbr());
        }
        return toDTO(animalRepository.save(toEntity(dto, animal)));
    }

    @Transactional
    public void excluir(Long id) {
        animalRepository.delete(findById(id));
    }

    // ─── Raças ───
    public List<RacaDTO> listarRacas() {
        return racaRepository.findByAtivoTrue().stream()
            .map(r -> new RacaDTO(r.getId(), r.getNome(), r.getDescricao(), r.isAtivo()))
            .toList();
    }

    @Transactional
    public RacaDTO cadastrarRaca(RacaDTO dto) {
        var raca = Raca.builder().nome(dto.nome()).descricao(dto.descricao()).ativo(true).build();
        raca = racaRepository.save(raca);
        return new RacaDTO(raca.getId(), raca.getNome(), raca.getDescricao(), raca.isAtivo());
    }

    // ─── Helpers ───
    private Animal findById(Long id) {
        return animalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
    }

    private Animal toEntity(AnimalDTO dto, Animal animal) {
        animal.setNbr(dto.nbr());
        animal.setNome(dto.nome());
        animal.setSexo(dto.sexo());
        animal.setStatus(dto.status() != null ? dto.status() : StatusAnimal.ATIVO);
        animal.setDataNascimento(dto.dataNascimento());
        animal.setPesoEntrada(dto.pesoEntrada());
        animal.setObservacoes(dto.observacoes());
        if (dto.racaId() != null) {
            animal.setRaca(racaRepository.findById(dto.racaId()).orElse(null));
        }
        if (dto.paiId() != null) {
            animal.setPai(animalRepository.findById(dto.paiId()).orElse(null));
        }
        if (dto.maeId() != null) {
            animal.setMae(animalRepository.findById(dto.maeId()).orElse(null));
        }
        return animal;
    }

    public AnimalDTO toDTO(Animal a) {
        return new AnimalDTO(
            a.getId(), a.getNbr(), a.getNome(),
            a.getRaca() != null ? a.getRaca().getId() : null,
            a.getRaca() != null ? a.getRaca().getNome() : null,
            a.getSexo(), a.getStatus(), a.getDataNascimento(),
            a.getPai() != null ? a.getPai().getId() : null,
            a.getPai() != null ? a.getPai().getNbr() : null,
            a.getMae() != null ? a.getMae().getId() : null,
            a.getMae() != null ? a.getMae().getNbr() : null,
            a.getPesoEntrada(), a.getObservacoes()
        );
    }
}
