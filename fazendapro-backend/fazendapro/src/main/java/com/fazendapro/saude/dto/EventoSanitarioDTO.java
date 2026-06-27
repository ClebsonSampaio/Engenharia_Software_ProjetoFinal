package com.fazendapro.saude.dto;

import com.fazendapro.saude.model.TipoEventoSanitario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EventoSanitarioDTO(
    Long id,
    @NotNull Long animalId,
    String animalNbr,
    @NotNull TipoEventoSanitario tipo,
    @NotBlank String descricao,
    @NotNull LocalDate dataEvento,
    LocalDate dataRetorno,
    Integer carenciaDias,
    LocalDate dataFimCarencia,
    String veterinario,
    String produtoAplicado,
    String dose,
    BigDecimal custo,
    String observacoes
) {}
