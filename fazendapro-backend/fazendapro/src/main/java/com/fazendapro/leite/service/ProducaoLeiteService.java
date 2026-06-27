package com.fazendapro.leite.service;

import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.exception.BusinessException;
import com.fazendapro.exception.ResourceNotFoundException;
import com.fazendapro.leite.dto.ProducaoLeiteDTO;
import com.fazendapro.leite.model.ClassificacaoLeite;
import com.fazendapro.leite.model.ProducaoLeite;
import com.fazendapro.leite.repository.ProducaoLeiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProducaoLeiteService {

    private final ProducaoLeiteRepository producaoRepo;
    private final AnimalRepository animalRepo;

    public List<ProducaoLeiteDTO> listarPorAnimal(Long animalId) {
        return producaoRepo.findByAnimalIdOrderByDataDesc(animalId).stream().map(this::toDTO).toList();
    }

    public List<ProducaoLeiteDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return producaoRepo.findByDataBetweenOrderByDataDesc(inicio, fim).stream().map(this::toDTO).toList();
    }

    public BigDecimal totalPorPeriodo(LocalDate inicio, LocalDate fim) {
        BigDecimal total = producaoRepo.somarProducaoPorPeriodo(inicio, fim);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public ProducaoLeiteDTO registrar(ProducaoLeiteDTO dto) {
        var animal = animalRepo.findById(dto.animalId())
            .orElseThrow(() -> new ResourceNotFoundException("Animal", dto.animalId()));

        if (animal.getSexo().name().equals("MACHO")) {
            throw new BusinessException("Machos não produzem leite.");
        }
        if (producaoRepo.findByAnimalIdAndData(dto.animalId(), dto.data()).isPresent()) {
            throw new BusinessException("Produção já registrada para este animal nesta data.");
        }

        var producao = ProducaoLeite.builder()
            .animal(animal)
            .data(dto.data())
            .quantidadeManha(dto.quantidadeManha() != null ? dto.quantidadeManha() : BigDecimal.ZERO)
            .quantidadeTarde(dto.quantidadeTarde() != null ? dto.quantidadeTarde() : BigDecimal.ZERO)
            .classificacao(dto.classificacao() != null ? dto.classificacao() : ClassificacaoLeite.NORMAL)
            .ccs(dto.ccs())
            .cbt(dto.cbt())
            .observacoes(dto.observacoes())
            .build();
        return toDTO(producaoRepo.save(producao));
    }

    @Transactional
    public ProducaoLeiteDTO atualizar(Long id, ProducaoLeiteDTO dto) {
        var producao = producaoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produção de leite", id));
        producao.setQuantidadeManha(dto.quantidadeManha());
        producao.setQuantidadeTarde(dto.quantidadeTarde());
        producao.setClassificacao(dto.classificacao());
        producao.setCcs(dto.ccs());
        producao.setCbt(dto.cbt());
        producao.setObservacoes(dto.observacoes());
        return toDTO(producaoRepo.save(producao));
    }

    @Transactional
    public void excluir(Long id) {
        if (!producaoRepo.existsById(id)) throw new ResourceNotFoundException("Produção de leite", id);
        producaoRepo.deleteById(id);
    }

    private ProducaoLeiteDTO toDTO(ProducaoLeite p) {
        return new ProducaoLeiteDTO(p.getId(), p.getAnimal().getId(),
            p.getAnimal().getNbr(), p.getAnimal().getNome(),
            p.getData(), p.getQuantidadeManha(), p.getQuantidadeTarde(),
            p.getQuantidadeTotal(), p.getClassificacao(), p.getCcs(), p.getCbt(), p.getObservacoes());
    }
}
