package com.fazendapro.auth.dto;

public record LoginResponse(
    String token,
    String tipo,
    Long id,
    String nome,
    String email,
    String perfil
) {
    public LoginResponse(String token, Long id, String nome, String email, String perfil) {
        this(token, "Bearer", id, nome, email, perfil);
    }
}
