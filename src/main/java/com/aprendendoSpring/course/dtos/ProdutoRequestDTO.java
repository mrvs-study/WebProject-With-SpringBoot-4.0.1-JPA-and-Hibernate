package com.aprendendoSpring.course.dtos;

import java.time.Instant;

public record ProdutoRequestDTO(
    String nome,
    String categoria,
    Double preco,
    Instant dataDeValidade,
    String imgUrlProduto
) {
}