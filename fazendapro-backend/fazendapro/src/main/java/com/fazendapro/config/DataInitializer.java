package com.fazendapro.config;

import com.fazendapro.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Corrige a senha do admin caso o hash do SQL de migração esteja inválido
        usuarioRepository.findByEmail("admin@fazendapro.com").ifPresent(admin -> {
            if (!passwordEncoder.matches("admin123", admin.getSenha())) {
                admin.setSenha(passwordEncoder.encode("admin123"));
                usuarioRepository.save(admin);
                log.info("✅ Senha do admin corrigida com sucesso");
            } else {
                log.info("✅ Senha do admin já está correta");
            }
        });
    }
}
