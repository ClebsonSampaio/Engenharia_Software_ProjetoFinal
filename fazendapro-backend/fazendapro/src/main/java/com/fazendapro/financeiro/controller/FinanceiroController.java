package com.fazendapro.financeiro.controller;

import com.fazendapro.financeiro.dto.LancamentoFinanceiroDTO;
import com.fazendapro.financeiro.dto.ResumoFinanceiroDTO;
import com.fazendapro.financeiro.service.FinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
public class FinanceiroController {
    private final FinanceiroService service;

    @GetMapping
    public ResponseEntity<List<LancamentoFinanceiroDTO>> listarPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<LancamentoFinanceiroDTO>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroDTO> resumo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(service.resumo(inicio, fim));
    }

    @PostMapping
    public ResponseEntity<LancamentoFinanceiroDTO> cadastrar(@Valid @RequestBody LancamentoFinanceiroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<LancamentoFinanceiroDTO> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(service.pagar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
