package com.aprendendoSpring.course.dtos;

public record EstoqueRequestDTO(
        Long produtoId,
        Integer quantidadeAtual,
        Integer quantidadeMaxima,
        String localizacao
) {
}
