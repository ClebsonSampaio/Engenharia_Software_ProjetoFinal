package com.fazendapro.reproducao.controller;

import com.fazendapro.reproducao.dto.ReproducaoDTO;
import com.fazendapro.reproducao.service.ReproducaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reproducao")
@RequiredArgsConstructor
public class ReproducaoController {
    private final ReproducaoService service;

    @GetMapping("/femea/{femeaId}")
    public ResponseEntity<List<ReproducaoDTO>> listarPorFemea(@PathVariable Long femeaId) {
        return ResponseEntity.ok(service.listarPorFemea(femeaId));
    }

    @GetMapping("/partos-previstos")
    public ResponseEntity<List<ReproducaoDTO>> partosPrevistos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.partosPrevistos(inicio, fim));
    }

    @GetMapping("/diagnostico-pendente")
    public ResponseEntity<List<ReproducaoDTO>> diagnosticosPendentes() {
        return ResponseEntity.ok(service.diagnosticosPendentes());
    }

    @PostMapping
    public ResponseEntity<ReproducaoDTO> registrar(@Valid @RequestBody ReproducaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReproducaoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ReproducaoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }
}
