package com.fazendapro.auth.dto;

import com.fazendapro.auth.model.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank @Email(message = "Email inválido")
    String email,

    @NotBlank @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String senha,

    @NotNull(message = "Perfil é obrigatório")
    PerfilUsuario perfil
) {}
