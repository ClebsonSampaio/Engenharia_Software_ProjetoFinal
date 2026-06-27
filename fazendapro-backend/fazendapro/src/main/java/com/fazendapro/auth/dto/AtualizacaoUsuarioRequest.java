package com.fazendapro.auth.dto;

import com.fazendapro.auth.model.PerfilUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizacaoUsuarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotNull(message = "Perfil é obrigatório") PerfilUsuario perfil
) {}
