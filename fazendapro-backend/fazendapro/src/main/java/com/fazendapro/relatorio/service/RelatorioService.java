package com.fazendapro.relatorio.service;

import com.fazendapro.animal.model.Animal;
import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.financeiro.model.LancamentoFinanceiro;
import com.fazendapro.financeiro.model.TipoLancamento;
import com.fazendapro.financeiro.repository.LancamentoFinanceiroRepository;
import com.fazendapro.leite.model.ProducaoLeite;
import com.fazendapro.leite.repository.ProducaoLeiteRepository;
// OpenPDF — imports específicos (evita conflito com Row/Cell do Apache POI)

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final AnimalRepository animalRepository;
    private final ProducaoLeiteRepository producaoLeiteRepository;
    private final LancamentoFinanceiroRepository lancamentoRepository;

    private static final DateTimeFormatter BR   = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COR_DARK  = new Color(30,41,59);
    private static final Color COR_VERDE = new Color(21,128,61);
    private static final Color COR_CINZA = new Color(248,250,252);
    private static final Color COR_RED   = new Color(220,38,38);
    private static final Color COR_WHITE = Color.WHITE;
    private static final Color COR_BORDA = new Color(226,232,240);

    // ─── REBANHO ────────────────────────────────────────────────
    public byte[] gerarRebanhoPdf() throws Exception {
        List<Animal> list = animalRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 50, 40);
        PdfWriter w = PdfWriter.getInstance(doc, baos);
        w.setPageEvent(new Rodape("Relatório do Rebanho"));
        doc.open();
        cabecalho(doc, "RELATÓRIO DO REBANHO",
            "Total: " + list.size() + " animais  |  Gerado em: " + LocalDate.now().format(BR));

        PdfPTable t = new PdfPTable(new float[]{2f,3f,3f,1.5f,2f,2.5f,2f});
        t.setWidthPercentage(100); t.setSpacingBefore(14);
        for (String h : new String[]{"NBR","Nome","Raça","Sexo","Status","Nascimento","Peso (kg)"}) thCell(t, h);

        boolean alt = false;
        for (Animal a : list) {
            tdCell(t, a.getNbr(), alt, Element.ALIGN_LEFT);
            tdCell(t, nvl(a.getNome()), alt, Element.ALIGN_LEFT);
            tdCell(t, a.getRaca()!=null?a.getRaca().getNome():"—", alt, Element.ALIGN_LEFT);
            tdCell(t, a.getSexo().name(), alt, Element.ALIGN_CENTER);
            tdCell(t, a.getStatus().name(), alt, Element.ALIGN_CENTER);
            tdCell(t, a.getDataNascimento()!=null?a.getDataNascimento().format(BR):"—", alt, Element.ALIGN_CENTER);
            tdCell(t, a.getPesoEntrada()!=null?a.getPesoEntrada().toPlainString():"—", alt, Element.ALIGN_RIGHT);
            alt=!alt;
        }
        rodapeTotal(t,"TOTAL: " + list.size() + " animais", 7);
        doc.add(t); doc.close(); return baos.toByteArray();
    }

    public byte[] gerarRebanhoExcel() throws Exception {
        List<Animal> list = animalRepository.findAll();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet("Rebanho");
        String[] cols = {"NBR","Nome","Raça","Sexo","Status","Nascimento","Peso (kg)"};
        initSheet(wb, s, "FazendaPro — Relatório do Rebanho",
            "Gerado em: " + LocalDate.now().format(BR) + " | Total: " + list.size() + " animais", cols);
        var dado = estiloDado(wb,false); var dadoAlt = estiloDado(wb,true);
        for (int i=0;i<list.size();i++) {
            Animal a = list.get(i);
            Row r = s.createRow(4+i);
            var st = i%2==0?dado:dadoAlt;
            xcel(r,0,a.getNbr(),st); xcel(r,1,nvl(a.getNome()),st);
            xcel(r,2,a.getRaca()!=null?a.getRaca().getNome():"—",st);
            xcel(r,3,a.getSexo().name(),st); xcel(r,4,a.getStatus().name(),st);
            xcel(r,5,a.getDataNascimento()!=null?a.getDataNascimento().format(BR):"—",st);
            xcel(r,6,a.getPesoEntrada()!=null?a.getPesoEntrada().toPlainString():"—",st);
        }
        footerRow(wb,s,4+list.size()+1,"TOTAL: "+list.size()+" animais",7);
        for(int i=0;i<cols.length;i++) s.autoSizeColumn(i);
        return bytes(wb);
    }

    // ─── PRODUÇÃO ───────────────────────────────────────────────
    public byte[] gerarProducaoPdf(LocalDate inicio, LocalDate fim) throws Exception {
        List<ProducaoLeite> list = producaoLeiteRepository.findByDataBetweenOrderByDataDesc(inicio, fim);
        BigDecimal total = list.stream().map(p->p.getQuantidadeTotal()!=null?p.getQuantidadeTotal():BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 50, 40);
        PdfWriter w = PdfWriter.getInstance(doc, baos);
        w.setPageEvent(new Rodape("Relatório de Produção de Leite"));
        doc.open();
        cabecalho(doc, "RELATÓRIO DE PRODUÇÃO DE LEITE",
            "Período: " + inicio.format(BR) + " a " + fim.format(BR) +
            "  |  Registros: " + list.size() + "  |  Total: " + total.toPlainString() + " L" +
            "  |  Gerado em: " + LocalDate.now().format(BR));

        PdfPTable t = new PdfPTable(new float[]{2f,2f,3f,2f,2f,2f,2.5f});
        t.setWidthPercentage(100); t.setSpacingBefore(14);
        for (String h : new String[]{"Data","NBR","Nome","Manhã (L)","Tarde (L)","Total (L)","Classificação"}) thCell(t,h);

        boolean alt=false;
        for (ProducaoLeite p : list) {
            tdCell(t, p.getData().format(BR), alt, Element.ALIGN_CENTER);
            tdCell(t, p.getAnimal().getNbr(), alt, Element.ALIGN_CENTER);
            tdCell(t, nvl(p.getAnimal().getNome()), alt, Element.ALIGN_LEFT);
            tdCell(t, fmt(p.getQuantidadeManha()), alt, Element.ALIGN_RIGHT);
            tdCell(t, fmt(p.getQuantidadeTarde()), alt, Element.ALIGN_RIGHT);
            tdCell(t, fmt(p.getQuantidadeTotal()), alt, Element.ALIGN_RIGHT);
            tdCell(t, p.getClassificacao().name(), alt, Element.ALIGN_CENTER);
            alt=!alt;
        }
        rodapeTotal(t, "TOTAL GERAL: " + total.toPlainString() + " L", 7);
        doc.add(t); doc.close(); return baos.toByteArray();
    }

    public byte[] gerarProducaoExcel(LocalDate inicio, LocalDate fim) throws Exception {
        List<ProducaoLeite> list = producaoLeiteRepository.findByDataBetweenOrderByDataDesc(inicio, fim);
        BigDecimal total = list.stream().map(p->p.getQuantidadeTotal()!=null?p.getQuantidadeTotal():BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet("Producao");
        String[] cols = {"Data","NBR","Nome","Manhã (L)","Tarde (L)","Total (L)","Classificação"};
        initSheet(wb,s,"FazendaPro — Produção de Leite",
            "Período: "+inicio.format(BR)+" a "+fim.format(BR)+" | Total: "+total.toPlainString()+" L | Registros: "+list.size(), cols);
        var dado=estiloDado(wb,false); var dadoAlt=estiloDado(wb,true);
        for (int i=0;i<list.size();i++) {
            ProducaoLeite p=list.get(i); Row r=s.createRow(4+i); var st=i%2==0?dado:dadoAlt;
            xcel(r,0,p.getData().format(BR),st); xcel(r,1,p.getAnimal().getNbr(),st);
            xcel(r,2,nvl(p.getAnimal().getNome()),st); xcel(r,3,fmt(p.getQuantidadeManha()),st);
            xcel(r,4,fmt(p.getQuantidadeTarde()),st); xcel(r,5,fmt(p.getQuantidadeTotal()),st);
            xcel(r,6,p.getClassificacao().name(),st);
        }
        footerRow(wb,s,4+list.size()+1,"TOTAL: "+total.toPlainString()+" L",7);
        for(int i=0;i<cols.length;i++) s.autoSizeColumn(i);
        return bytes(wb);
    }

    // ─── FINANCEIRO / DRE ───────────────────────────────────────
    public byte[] gerarFinanceiroPdf(LocalDate inicio, LocalDate fim) throws Exception {
        List<LancamentoFinanceiro> list = lancamentoRepository
            .findByDataLancamentoBetweenOrderByDataLancamentoDesc(inicio, fim);
        BigDecimal rec = soma(list, TipoLancamento.RECEITA);
        BigDecimal des = soma(list, TipoLancamento.DESPESA);
        BigDecimal sal = rec.subtract(des);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 50, 40);
        PdfWriter w = PdfWriter.getInstance(doc, baos);
        w.setPageEvent(new Rodape("Demonstrativo de Resultado (DRE)"));
        doc.open();
        cabecalho(doc, "DEMONSTRATIVO DE RESULTADO — DRE",
            "Período: "+inicio.format(BR)+" a "+fim.format(BR)+" | Gerado em: "+LocalDate.now().format(BR));

        // Resumo
        PdfPTable res = new PdfPTable(3); res.setWidthPercentage(80);
        res.setSpacingBefore(14); res.setSpacingAfter(14);
        res.setHorizontalAlignment(Element.ALIGN_CENTER);
        celulaResumo(res,"TOTAL RECEITAS","R$ "+rec.toPlainString(),COR_VERDE);
        celulaResumo(res,"TOTAL DESPESAS","R$ "+des.toPlainString(),COR_RED);
        celulaResumo(res,"SALDO","R$ "+sal.toPlainString(),sal.compareTo(BigDecimal.ZERO)>=0?COR_VERDE:COR_RED);
        doc.add(res);

        PdfPTable t = new PdfPTable(new float[]{2f,1.5f,2.5f,4.5f,2.5f,2f,1.5f});
        t.setWidthPercentage(100);
        for (String h : new String[]{"Data","Tipo","Categoria","Descrição","Valor (R$)","Vencimento","Status"}) thCell(t,h);

        boolean alt=false;
        for (LancamentoFinanceiro l : list) {
            boolean isRec = l.getTipo()==TipoLancamento.RECEITA;
            tdCell(t, l.getDataLancamento().format(BR), alt, Element.ALIGN_CENTER);
            tdCellCor(t, l.getTipo().name(), alt, isRec?COR_VERDE:COR_RED);
            tdCell(t, l.getCategoria().name().replace("_"," "), alt, Element.ALIGN_LEFT);
            tdCell(t, l.getDescricao(), alt, Element.ALIGN_LEFT);
            tdCellCor(t, "R$ "+l.getValor().toPlainString(), alt, isRec?COR_VERDE:COR_RED);
            tdCell(t, l.getDataVencimento()!=null?l.getDataVencimento().format(BR):"—", alt, Element.ALIGN_CENTER);
            tdCell(t, l.isPago()?"Pago":"Pendente", alt, Element.ALIGN_CENTER);
            alt=!alt;
        }
        rodapeTotal(t,"SALDO DO PERÍODO: R$ "+sal.toPlainString(), 7);
        doc.add(t); doc.close(); return baos.toByteArray();
    }

    public byte[] gerarFinanceiroExcel(LocalDate inicio, LocalDate fim) throws Exception {
        List<LancamentoFinanceiro> list = lancamentoRepository
            .findByDataLancamentoBetweenOrderByDataLancamentoDesc(inicio, fim);
        BigDecimal rec = soma(list, TipoLancamento.RECEITA);
        BigDecimal des = soma(list, TipoLancamento.DESPESA);
        BigDecimal sal = rec.subtract(des);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet("DRE");
        String[] cols = {"Data","Tipo","Categoria","Descrição","Valor (R$)","Vencimento","Status"};
        initSheet(wb,s,"FazendaPro — DRE ("+inicio.format(BR)+" a "+fim.format(BR)+")",
            "Receitas: R$"+rec.toPlainString()+" | Despesas: R$"+des.toPlainString()+" | Saldo: R$"+sal.toPlainString(), cols);
        var dado=estiloDado(wb,false); var dadoAlt=estiloDado(wb,true);
        for (int i=0;i<list.size();i++) {
            LancamentoFinanceiro l=list.get(i); Row r=s.createRow(4+i); var st=i%2==0?dado:dadoAlt;
            xcel(r,0,l.getDataLancamento().format(BR),st); xcel(r,1,l.getTipo().name(),st);
            xcel(r,2,l.getCategoria().name().replace("_"," "),st); xcel(r,3,l.getDescricao(),st);
            xcel(r,4,"R$ "+l.getValor().toPlainString(),st);
            xcel(r,5,l.getDataVencimento()!=null?l.getDataVencimento().format(BR):"—",st);
            xcel(r,6,l.isPago()?"Pago":"Pendente",st);
        }
        int lastRow = 4+list.size()+1;
        var tot = estiloTotal(wb);
        footerRow(wb,s,lastRow,  "TOTAL RECEITAS: R$ "+rec.toPlainString(),7);
        footerRow(wb,s,lastRow+1,"TOTAL DESPESAS: R$ "+des.toPlainString(),7);
        footerRow(wb,s,lastRow+2,"SALDO: R$ "+sal.toPlainString(),7);
        for(int i=0;i<cols.length;i++) s.autoSizeColumn(i);
        return bytes(wb);
    }

    // ─── PDF HELPERS ────────────────────────────────────────────
    private void cabecalho(Document doc, String titulo, String info) throws Exception {
        doc.add(new Paragraph("🌾 FazendaPro",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, COR_VERDE)));
        Paragraph pT = new Paragraph(titulo,
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COR_DARK));
        pT.setSpacingBefore(4); doc.add(pT);
        Paragraph pI = new Paragraph(info,
            FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100,116,139)));
        pI.setSpacingBefore(4); pI.setSpacingAfter(8); doc.add(pI);
        doc.add(new Chunk(new LineSeparator(1f, 100f, COR_DARK, Element.ALIGN_LEFT, -2)));
    }

    private void thCell(PdfPTable t, String txt) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COR_WHITE);
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setBackgroundColor(COR_DARK); c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(7); c.setBorderColor(new Color(15,23,42)); t.addCell(c);
    }

    private void tdCell(PdfPTable t, String txt, boolean alt, int align) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9, COR_DARK);
        PdfPCell c = new PdfPCell(new Phrase(txt != null ? txt : "—", f));
        c.setBackgroundColor(alt ? COR_CINZA : COR_WHITE);
        c.setHorizontalAlignment(align);
        c.setPaddingLeft(6); c.setPaddingRight(6); c.setPaddingTop(5); c.setPaddingBottom(5);
        c.setBorderColor(COR_BORDA); t.addCell(c);
    }

    private void tdCellCor(PdfPTable t, String txt, boolean alt, Color cor) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, cor);
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setBackgroundColor(alt ? COR_CINZA : COR_WHITE);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(5); c.setPaddingLeft(6); c.setPaddingRight(6);
        c.setBorderColor(COR_BORDA); t.addCell(c);
    }

    private void rodapeTotal(PdfPTable t, String txt, int colspan) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COR_WHITE);
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setColspan(colspan); c.setBackgroundColor(COR_DARK);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c.setPadding(7); c.setPaddingRight(12); t.addCell(c);
    }

    private void celulaResumo(PdfPTable t, String titulo, String valor, Color cor) {
        Font fT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(100,116,139));
        Font fV = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, cor);
        Phrase p = new Phrase(); p.add(new Chunk(titulo+"\n", fT)); p.add(new Chunk(valor, fV));
        PdfPCell c = new PdfPCell(p);
        c.setBackgroundColor(COR_CINZA); c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(12); c.setBorderColor(COR_BORDA); t.addCell(c);
    }

    // ─── EXCEL HELPERS ──────────────────────────────────────────
    private void initSheet(XSSFWorkbook wb, XSSFSheet s, String titulo, String info, String[] cols) {
        var sTit = estiloTitulo(wb); var sCab = estiloCabecalho(wb);
        Row rT = s.createRow(0); rT.setHeightInPoints(28);
        Cell cT = rT.createCell(0); cT.setCellValue(titulo); cT.setCellStyle(sTit);
        s.addMergedRegion(new CellRangeAddress(0,0,0,cols.length-1));
        Row rI = s.createRow(1);
        Cell cI = rI.createCell(0); cI.setCellValue(info);
        s.addMergedRegion(new CellRangeAddress(1,1,0,cols.length-1));
        Row rC = s.createRow(3); rC.setHeightInPoints(20);
        for (int i=0;i<cols.length;i++) xcel(rC, i, cols[i], sCab);
    }

    private void footerRow(XSSFWorkbook wb, XSSFSheet s, int rowIdx, String txt, int colSpan) {
        var st = estiloTotal(wb);
        Row r = s.createRow(rowIdx);
        Cell c = r.createCell(0); c.setCellValue(txt); c.setCellStyle(st);
        s.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, colSpan-1));
    }

    private XSSFCellStyle estiloTitulo(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)41,(byte)59}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT); s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)14);
        f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null)); s.setFont(f); return s;
    }

    private XSSFCellStyle estiloCabecalho(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)21,(byte)128,(byte)61}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10);
        f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null)); s.setFont(f); return s;
    }

    private XSSFCellStyle estiloDado(XSSFWorkbook wb, boolean alt) {
        XSSFCellStyle s = wb.createCellStyle();
        if (alt) { s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)248,(byte)250,(byte)252}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); }
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)226,(byte)232,(byte)240}, null));
        XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short)10); s.setFont(f); return s;
    }

    private XSSFCellStyle estiloTotal(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)41,(byte)59}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)11);
        f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null)); s.setFont(f); return s;
    }

    private Cell xcel(Row r, int col, String val, CellStyle st) {
        Cell c = r.createCell(col); c.setCellValue(val != null ? val : ""); if (st!=null) c.setCellStyle(st); return c;
    }

    private byte[] bytes(XSSFWorkbook wb) throws Exception {
        ByteArrayOutputStream b = new ByteArrayOutputStream(); wb.write(b); wb.close(); return b.toByteArray();
    }

    private String nvl(String s) { return s != null ? s : "—"; }
    private String fmt(BigDecimal v) { return v != null ? v.toPlainString() : "0.00"; }
    private BigDecimal soma(List<LancamentoFinanceiro> l, TipoLancamento tipo) {
        return l.stream().filter(x -> x.getTipo()==tipo)
            .map(x -> x.getValor()!=null?x.getValor():BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static class Rodape extends PdfPageEventHelper {
        private final String titulo;
        Rodape(String t) { this.titulo = t; }
        @Override public void onEndPage(PdfWriter writer, Document doc) {
            Font f = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(148,163,184));
            String txt = "FazendaPro  |  " + titulo + "  |  Página " + writer.getPageNumber();
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                new Phrase(txt, f), (doc.right()-doc.left())/2+doc.leftMargin(), doc.bottom()-8, 0);
        }
    }
}
