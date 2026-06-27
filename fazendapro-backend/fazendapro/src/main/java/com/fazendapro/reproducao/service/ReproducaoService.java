package com.fazendapro.reproducao.service;

import com.fazendapro.animal.model.SexoAnimal;
import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.exception.BusinessException;
import com.fazendapro.exception.ResourceNotFoundException;
import com.fazendapro.reproducao.dto.ReproducaoDTO;
import com.fazendapro.reproducao.model.DiagnosticoGestacao;
import com.fazendapro.reproducao.model.Reproducao;
import com.fazendapro.reproducao.repository.ReproducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReproducaoService {

    private final ReproducaoRepository reproducaoRepo;
    private final AnimalRepository animalRepo;

    public List<ReproducaoDTO> listarPorFemea(Long femeaId) {
        return reproducaoRepo.findByFemeaIdOrderByDataCoberturaDesc(femeaId).stream().map(this::toDTO).toList();
    }

    public List<ReproducaoDTO> partosPrevistos(LocalDate inicio, LocalDate fim) {
        return reproducaoRepo.findPartosPrevistos(inicio, fim).stream().map(this::toDTO).toList();
    }

    public List<ReproducaoDTO> diagnosticosPendentes() {
        return reproducaoRepo.findDiagnosticoPendente().stream().map(this::toDTO).toList();
    }

    @Transactional
    public ReproducaoDTO registrar(ReproducaoDTO dto) {
        var femea = animalRepo.findById(dto.femeaId())
            .orElseThrow(() -> new ResourceNotFoundException("Animal", dto.femeaId()));
        if (femea.getSexo() != SexoAnimal.FEMEA) {
            throw new BusinessException("Reprodução só pode ser registrada para fêmeas.");
        }
        var repro = Reproducao.builder()
            .femea(femea).tipoCobertura(dto.tipoCobertura())
            .dataCobertura(dto.dataCobertura())
            .nomeTourouExterno(dto.nomeTourouExterno())
            .semenCodigo(dto.semenCodigo())
            .diagnosticoGestacao(DiagnosticoGestacao.PENDENTE)
            .observacoes(dto.observacoes())
            .build();
        if (dto.touroId() != null) {
            repro.setTouro(animalRepo.findById(dto.touroId()).orElse(null));
        }
        return toDTO(reproducaoRepo.save(repro));
    }

    @Transactional
    public ReproducaoDTO atualizar(Long id, ReproducaoDTO dto) {
        var repro = reproducaoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reprodução", id));
        repro.setDiagnosticoGestacao(dto.diagnosticoGestacao());
        repro.setDataDiagnostico(dto.dataDiagnostico());
        repro.setDataParto(dto.dataParto());
        repro.setResultado(dto.resultado());
        repro.setObservacoes(dto.observacoes());
        if (dto.criaId() != null) {
            repro.setCria(animalRepo.findById(dto.criaId()).orElse(null));
        }
        return toDTO(reproducaoRepo.save(repro));
    }

    private ReproducaoDTO toDTO(Reproducao r) {
        return new ReproducaoDTO(r.getId(),
            r.getFemea().getId(), r.getFemea().getNbr(),
            r.getTipoCobertura(), r.getDataCobertura(), r.getDataDpp(),
            r.getTouro() != null ? r.getTouro().getId() : null,
            r.getTouro() != null ? r.getTouro().getNbr() : null,
            r.getNomeTourouExterno(), r.getSemenCodigo(),
            r.getDiagnosticoGestacao(), r.getDataDiagnostico(), r.getDataParto(),
            r.getCria() != null ? r.getCria().getId() : null,
            r.getResultado(), r.getObservacoes());
    }
}
