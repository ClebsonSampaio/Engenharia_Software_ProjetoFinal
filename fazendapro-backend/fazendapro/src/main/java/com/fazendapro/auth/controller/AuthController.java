package com.fazendapro.auth.controller;

import com.fazendapro.auth.dto.*;
import com.fazendapro.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ─── Público ───
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ─── Admin: gerenciar usuários ───
    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(request));
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarTodos());
    }

    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoUsuarioRequest request) {
        return ResponseEntity.ok(authService.atualizar(id, request));
    }

    @PatchMapping("/usuarios/{id}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(authService.ativar(id));
    }

    @PatchMapping("/usuarios/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(authService.desativar(id));
    }

    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        authService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
