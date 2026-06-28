package com.fazendapro.relatorio.controller;

import com.fazendapro.relatorio.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    // ─── REBANHO ─────────────────────────────────────────────────
    @GetMapping("/rebanho/pdf")
    public ResponseEntity<byte[]> rebanhoPdf() throws Exception {
        return pdf(relatorioService.gerarRebanhoPdf(), "fazendapro-rebanho.pdf");
    }

    @GetMapping("/rebanho/excel")
    public ResponseEntity<byte[]> rebanhoExcel() throws Exception {
        return excel(relatorioService.gerarRebanhoExcel(), "fazendapro-rebanho.xlsx");
    }

    // ─── PRODUÇÃO DE LEITE ───────────────────────────────────────
    @GetMapping("/producao/pdf")
    public ResponseEntity<byte[]> producaoPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws Exception {
        return pdf(relatorioService.gerarProducaoPdf(inicio, fim),
                "fazendapro-producao-" + inicio + "-" + fim + ".pdf");
    }

    @GetMapping("/producao/excel")
    public ResponseEntity<byte[]> producaoExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws Exception {
        return excel(relatorioService.gerarProducaoExcel(inicio, fim),
                "fazendapro-producao-" + inicio + "-" + fim + ".xlsx");
    }

    // ─── FINANCEIRO / DRE ────────────────────────────────────────
    @GetMapping("/financeiro/pdf")
    public ResponseEntity<byte[]> financeiroPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws Exception {
        return pdf(relatorioService.gerarFinanceiroPdf(inicio, fim),
                "fazendapro-dre-" + inicio + "-" + fim + ".pdf");
    }

    @GetMapping("/financeiro/excel")
    public ResponseEntity<byte[]> financeiroExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws Exception {
        return excel(relatorioService.gerarFinanceiroExcel(inicio, fim),
                "fazendapro-dre-" + inicio + "-" + fim + ".xlsx");
    }

    // ─── Helpers ─────────────────────────────────────────────────
    private ResponseEntity<byte[]> pdf(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .headers(headers("application/pdf", filename))
                .body(bytes);
    }

    private ResponseEntity<byte[]> excel(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .headers(headers(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        filename))
                .body(bytes);
    }

    private HttpHeaders headers(String contentType, String filename) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.parseMediaType(contentType));
        h.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        h.add("Access-Control-Expose-Headers", "Content-Disposition");
        return h;
    }
}
