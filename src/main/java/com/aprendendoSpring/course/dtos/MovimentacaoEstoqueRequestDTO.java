package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.enums.TipoMovimentacao;

public record MovimentacaoEstoqueRequestDTO(
        Long estoqueId,
        TipoMovimentacao tipoMovimentacao,
        Integer quantidade,
        String motivo
) {
}