package com.fazendapro.estoque.controller;

import com.fazendapro.estoque.dto.MovimentacaoEstoqueDTO;
import com.fazendapro.estoque.dto.ProdutoEstoqueDTO;
import com.fazendapro.estoque.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {
    private final EstoqueService service;

    @GetMapping
    public ResponseEntity<List<ProdutoEstoqueDTO>> listarProdutos() {
        return ResponseEntity.ok(service.listarProdutos());
    }

    @GetMapping("/alertas/minimo")
    public ResponseEntity<List<ProdutoEstoqueDTO>> abaixoMinimo() {
        return ResponseEntity.ok(service.listarAbaixoMinimo());
    }

    @GetMapping("/alertas/vencimento")
    public ResponseEntity<List<ProdutoEstoqueDTO>> proximosVencimento() {
        return ResponseEntity.ok(service.listarProximosVencimento());
    }

    @PostMapping("/produtos")
    public ResponseEntity<ProdutoEstoqueDTO> cadastrarProduto(@Valid @RequestBody ProdutoEstoqueDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarProduto(dto));
    }

    @PostMapping("/movimentacoes")
    public ResponseEntity<MovimentacaoEstoqueDTO> movimentar(@Valid @RequestBody MovimentacaoEstoqueDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.movimentar(dto));
    }
}
