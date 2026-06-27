package com.fazendapro.animal.dto;

import com.fazendapro.animal.model.SexoAnimal;
import com.fazendapro.animal.model.StatusAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalDTO(
    Long id,
    @NotBlank(message = "NBR (número de brinco) é obrigatório") String nbr,
    String nome,
    Long racaId,
    String racaNome,
    @NotNull(message = "Sexo é obrigatório") SexoAnimal sexo,
    StatusAnimal status,
    LocalDate dataNascimento,
    Long paiId,
    String paiNbr,
    Long maeId,
    String maeNbr,
    BigDecimal pesoEntrada,
    String observacoes
) {}
