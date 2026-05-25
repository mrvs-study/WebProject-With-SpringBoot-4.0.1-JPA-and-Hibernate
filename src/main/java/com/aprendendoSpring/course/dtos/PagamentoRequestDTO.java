package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.enums.FormaPagamento;

public record PagamentoRequestDTO(
        Long vendaId,
        Double valor,
        FormaPagamento formaPagamento
) {
}