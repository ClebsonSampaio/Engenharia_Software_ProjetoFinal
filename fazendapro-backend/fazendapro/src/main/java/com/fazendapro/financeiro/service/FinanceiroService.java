package com.fazendapro.financeiro.service;

import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.exception.ResourceNotFoundException;
import com.fazendapro.financeiro.dto.LancamentoFinanceiroDTO;
import com.fazendapro.financeiro.dto.ResumoFinanceiroDTO;
import com.fazendapro.financeiro.model.LancamentoFinanceiro;
import com.fazendapro.financeiro.model.TipoLancamento;
import com.fazendapro.financeiro.repository.LancamentoFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoRepo;
    private final AnimalRepository animalRepo;

    public List<LancamentoFinanceiroDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return lancamentoRepo.findByDataLancamentoBetweenOrderByDataLancamentoDesc(inicio, fim)
            .stream().map(this::toDTO).toList();
    }

    public List<LancamentoFinanceiroDTO> listarPendentes() {
        return lancamentoRepo.findByPagoFalseOrderByDataVencimento().stream().map(this::toDTO).toList();
    }

    public ResumoFinanceiroDTO resumo(LocalDate inicio, LocalDate fim) {
        BigDecimal receitas = lancamentoRepo.somarPorTipoEPeriodo(TipoLancamento.RECEITA, inicio, fim);
        BigDecimal despesas = lancamentoRepo.somarPorTipoEPeriodo(TipoLancamento.DESPESA, inicio, fim);
        receitas = receitas != null ? receitas : BigDecimal.ZERO;
        despesas = despesas != null ? despesas : BigDecimal.ZERO;
        return new ResumoFinanceiroDTO(receitas, despesas, receitas.subtract(despesas),
            inicio + " a " + fim);
    }

    @Transactional
    public LancamentoFinanceiroDTO cadastrar(LancamentoFinanceiroDTO dto) {
        var lancamento = LancamentoFinanceiro.builder()
            .tipo(dto.tipo()).categoria(dto.categoria()).descricao(dto.descricao())
            .valor(dto.valor()).dataLancamento(dto.dataLancamento())
            .dataVencimento(dto.dataVencimento()).pago(dto.pago())
            .observacoes(dto.observacoes())
            .build();
        if (dto.animalId() != null) {
            lancamento.setAnimal(animalRepo.findById(dto.animalId()).orElse(null));
        }
        return toDTO(lancamentoRepo.save(lancamento));
    }

    @Transactional
    public LancamentoFinanceiroDTO pagar(Long id) {
        var lancamento = lancamentoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lançamento", id));
        lancamento.setPago(true);
        lancamento.setDataPagamento(LocalDate.now());
        return toDTO(lancamentoRepo.save(lancamento));
    }

    @Transactional
    public void excluir(Long id) {
        if (!lancamentoRepo.existsById(id)) throw new ResourceNotFoundException("Lançamento", id);
        lancamentoRepo.deleteById(id);
    }

    private LancamentoFinanceiroDTO toDTO(LancamentoFinanceiro l) {
        return new LancamentoFinanceiroDTO(l.getId(), l.getTipo(), l.getCategoria(),
            l.getDescricao(), l.getValor(), l.getDataLancamento(), l.getDataVencimento(),
            l.getDataPagamento(), l.isPago(),
            l.getAnimal() != null ? l.getAnimal().getId() : null, l.getObservacoes());
    }
}
