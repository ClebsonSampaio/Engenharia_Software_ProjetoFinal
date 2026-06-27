package com.fazendapro.animal.controller;

import com.fazendapro.animal.dto.AnimalDTO;
import com.fazendapro.animal.dto.RacaDTO;
import com.fazendapro.animal.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animais")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<AnimalDTO>> listar(@RequestParam(required = false) Boolean ativos) {
        var lista = Boolean.TRUE.equals(ativos) ? animalService.listarAtivos() : animalService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<AnimalDTO>> buscar(@RequestParam String termo) {
        return ResponseEntity.ok(animalService.buscar(termo));
    }

    // ─── NOVO: filhos do animal ───
    @GetMapping("/{id}/filhos")
    public ResponseEntity<List<AnimalDTO>> listarFilhos(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.listarFilhos(id));
    }

    @PostMapping
    public ResponseEntity<AnimalDTO> cadastrar(@Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.cadastrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AnimalDTO dto) {
        return ResponseEntity.ok(animalService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        animalService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Raças ───
    @GetMapping("/racas")
    public ResponseEntity<List<RacaDTO>> listarRacas() {
        return ResponseEntity.ok(animalService.listarRacas());
    }

    @PostMapping("/racas")
    public ResponseEntity<RacaDTO> cadastrarRaca(@Valid @RequestBody RacaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.cadastrarRaca(dto));
    }
}
