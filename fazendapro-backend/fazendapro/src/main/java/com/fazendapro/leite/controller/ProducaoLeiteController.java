package com.fazendapro.leite.controller;

import com.fazendapro.leite.dto.ProducaoLeiteDTO;
import com.fazendapro.leite.service.ProducaoLeiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/producao-leite")
@RequiredArgsConstructor
public class ProducaoLeiteController {

    private final ProducaoLeiteService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<ProducaoLeiteDTO>> listarPorAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(service.listarPorAnimal(animalId));
    }

    @GetMapping
    public ResponseEntity<List<ProducaoLeiteDTO>> listarPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> totalPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.totalPorPeriodo(inicio, fim));
    }

    @PostMapping
    public ResponseEntity<ProducaoLeiteDTO> registrar(@Valid @RequestBody ProducaoLeiteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProducaoLeiteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProducaoLeiteDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
