package com.fazendapro.estoque.repository;

import com.fazendapro.estoque.model.ProdutoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProdutoEstoqueRepository extends JpaRepository<ProdutoEstoque, Long> {

    List<ProdutoEstoque> findByAtivoTrue();

    @Query("SELECT p FROM ProdutoEstoque p WHERE p.ativo = true AND p.quantidadeAtual < p.quantidadeMinima")
    List<ProdutoEstoque> findAbaixoDoMinimo();

    @Query("SELECT p FROM ProdutoEstoque p WHERE p.ativo = true AND p.dataValidade IS NOT NULL AND p.dataValidade <= :dataLimite")
    List<ProdutoEstoque> findProximosVencimento(@Param("dataLimite") LocalDate dataLimite);
}
