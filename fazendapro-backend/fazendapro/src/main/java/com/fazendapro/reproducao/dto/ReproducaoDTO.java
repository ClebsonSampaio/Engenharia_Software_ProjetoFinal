package com.fazendapro.reproducao.dto;

import com.fazendapro.reproducao.model.DiagnosticoGestacao;
import com.fazendapro.reproducao.model.TipoCobertura;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReproducaoDTO(
    Long id,
    @NotNull Long femeaId,
    String femeaNbr,
    @NotNull TipoCobertura tipoCobertura,
    @NotNull LocalDate dataCobertura,
    LocalDate dataDpp,
    Long touroId,
    String touroNbr,
    String nomeTourouExterno,
    String semenCodigo,
    DiagnosticoGestacao diagnosticoGestacao,
    LocalDate dataDiagnostico,
    LocalDate dataParto,
    Long criaId,
    String resultado,
    String observacoes
) {}
