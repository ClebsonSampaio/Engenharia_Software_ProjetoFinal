package com.fazendapro.animal.dto;

import jakarta.validation.constraints.NotBlank;

public record RacaDTO(
    Long id,
    @NotBlank(message = "Nome da raça é obrigatório") String nome,
    String descricao,
    boolean ativo
) {}
