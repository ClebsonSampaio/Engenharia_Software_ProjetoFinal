package com.fazendapro.estoque.service;

import com.fazendapro.animal.repository.AnimalRepository;
import com.fazendapro.estoque.dto.MovimentacaoEstoqueDTO;
import com.fazendapro.estoque.dto.ProdutoEstoqueDTO;
import com.fazendapro.estoque.model.MovimentacaoEstoque;
import com.fazendapro.estoque.model.ProdutoEstoque;
import com.fazendapro.estoque.model.TipoMovimentacao;
import com.fazendapro.estoque.repository.MovimentacaoEstoqueRepository;
import com.fazendapro.estoque.repository.ProdutoEstoqueRepository;
import com.fazendapro.exception.BusinessException;
import com.fazendapro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstoqueService {

    private final ProdutoEstoqueRepository produtoRepo;
    private final MovimentacaoEstoqueRepository movRepo;
    private final AnimalRepository animalRepo;

    public List<ProdutoEstoqueDTO> listarProdutos() {
        return produtoRepo.findByAtivoTrue().stream().map(this::toProdutoDTO).toList();
    }

    public List<ProdutoEstoqueDTO> listarAbaixoMinimo() {
        return produtoRepo.findAbaixoDoMinimo().stream().map(this::toProdutoDTO).toList();
    }

    public List<ProdutoEstoqueDTO> listarProximosVencimento() {
        // Produtos que vencem nos próximos 30 dias
        LocalDate dataLimite = LocalDate.now().plusDays(30);
        return produtoRepo.findProximosVencimento(dataLimite).stream().map(this::toProdutoDTO).toList();
    }

    @Transactional
    public ProdutoEstoqueDTO cadastrarProduto(ProdutoEstoqueDTO dto) {
        var produto = ProdutoEstoque.builder()
            .nome(dto.nome()).categoria(dto.categoria()).unidade(dto.unidade())
            .quantidadeAtual(BigDecimal.ZERO)
            .quantidadeMinima(dto.quantidadeMinima() != null ? dto.quantidadeMinima() : BigDecimal.ZERO)
            .custoMedio(BigDecimal.ZERO).dataValidade(dto.dataValidade())
            .fabricante(dto.fabricante()).ativo(true)
            .build();
        return toProdutoDTO(produtoRepo.save(produto));
    }

    @Transactional
    public MovimentacaoEstoqueDTO movimentar(MovimentacaoEstoqueDTO dto) {
        var produto = produtoRepo.findById(dto.produtoId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto", dto.produtoId()));

        if (dto.tipo() == TipoMovimentacao.SAIDA
                && produto.getQuantidadeAtual().compareTo(dto.quantidade()) < 0) {
            throw new BusinessException("Estoque insuficiente. Disponível: "
                + produto.getQuantidadeAtual() + " " + produto.getUnidade());
        }

        if (dto.tipo() == TipoMovimentacao.ENTRADA) {
            // Custo Médio Ponderado (CMP)
            if (dto.custoUnitario() != null && dto.custoUnitario().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorAtual = produto.getQuantidadeAtual().multiply(produto.getCustoMedio());
                BigDecimal valorNovo  = dto.quantidade().multiply(dto.custoUnitario());
                BigDecimal qtdTotal   = produto.getQuantidadeAtual().add(dto.quantidade());
                BigDecimal novoCmp    = qtdTotal.compareTo(BigDecimal.ZERO) > 0
                    ? valorAtual.add(valorNovo).divide(qtdTotal, 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
                produto.setCustoMedio(novoCmp);
            }
            produto.setQuantidadeAtual(produto.getQuantidadeAtual().add(dto.quantidade()));
        } else {
            produto.setQuantidadeAtual(produto.getQuantidadeAtual().subtract(dto.quantidade()));
        }
        produtoRepo.save(produto);

        BigDecimal custoTotal = dto.custoUnitario() != null
            ? dto.quantidade().multiply(dto.custoUnitario()) : null;

        var mov = MovimentacaoEstoque.builder()
            .produto(produto).tipo(dto.tipo()).quantidade(dto.quantidade())
            .custoUnitario(dto.custoUnitario()).custoTotal(custoTotal)
            .dataMovimentacao(dto.dataMovimentacao())
            .motivo(dto.motivo()).numeroNota(dto.numeroNota())
            .build();
        if (dto.animalId() != null) {
            mov.setAnimal(animalRepo.findById(dto.animalId()).orElse(null));
        }
        var saved = movRepo.save(mov);
        return new MovimentacaoEstoqueDTO(saved.getId(), produto.getId(), produto.getNome(),
            saved.getTipo(), saved.getQuantidade(), saved.getCustoUnitario(), saved.getCustoTotal(),
            saved.getDataMovimentacao(), dto.animalId(), saved.getMotivo(), saved.getNumeroNota());
    }

    private ProdutoEstoqueDTO toProdutoDTO(ProdutoEstoque p) {
        return new ProdutoEstoqueDTO(p.getId(), p.getNome(), p.getCategoria(), p.getUnidade(),
            p.getQuantidadeAtual(), p.getQuantidadeMinima(), p.getCustoMedio(),
            p.getDataValidade(), p.getFabricante(), p.isAtivo(), p.estaBaixoEstoqueMinimo());
    }
}
