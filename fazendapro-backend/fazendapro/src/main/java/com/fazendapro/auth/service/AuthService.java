package com.fazendapro.auth.service;

import com.fazendapro.auth.dto.*;
import com.fazendapro.auth.model.Usuario;
import com.fazendapro.auth.repository.UsuarioRepository;
import com.fazendapro.exception.BusinessException;
import com.fazendapro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        var usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        var token = jwtService.generateToken(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNome(),
            usuario.getEmail(), usuario.getPerfil().name());
    }

    @Transactional
    public UsuarioDTO cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado: " + request.email());
        }
        var usuario = Usuario.builder()
            .nome(request.nome())
            .email(request.email())
            .senha(passwordEncoder.encode(request.senha()))
            .perfil(request.perfil())
            .ativo(true)
            .build();
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, AtualizacaoUsuarioRequest request) {
        var usuario = findById(id);
        usuario.setNome(request.nome());
        usuario.setPerfil(request.perfil());
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO desativar(Long id) {
        var usuario = findById(id);
        usuario.setAtivo(false);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO ativar(Long id) {
        var usuario = findById(id);
        usuario.setAtivo(true);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        usuarioRepository.delete(findById(id));
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(),
            u.getPerfil(), u.isAtivo(), u.getCriadoEm());
    }
}
