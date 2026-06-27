package com.fazendapro.saude.controller;

import com.fazendapro.saude.dto.EventoSanitarioDTO;
import com.fazendapro.saude.service.EventoSanitarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/saude")
@RequiredArgsConstructor
public class EventoSanitarioController {
    private final EventoSanitarioService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<EventoSanitarioDTO>> listarPorAnimal(@PathVariable Long animalId) {
        return ResponseEntity.ok(service.listarPorAnimal(animalId));
    }

    @GetMapping("/em-carencia")
    public ResponseEntity<List<EventoSanitarioDTO>> emCarencia() {
        return ResponseEntity.ok(service.listarEmCarencia());
    }

    @PostMapping
    public ResponseEntity<EventoSanitarioDTO> registrar(@Valid @RequestBody EventoSanitarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
