package com.fazendapro.auth.dto;

import com.fazendapro.auth.model.PerfilUsuario;
import java.time.LocalDateTime;

public record UsuarioDTO(
    Long id,
    String nome,
    String email,
    PerfilUsuario perfil,
    boolean ativo,
    LocalDateTime criadoEm
) {}
