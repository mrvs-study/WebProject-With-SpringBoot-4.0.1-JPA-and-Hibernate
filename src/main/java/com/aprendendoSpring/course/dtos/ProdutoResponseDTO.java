package com.aprendendoSpring.course.dtos;

import java.time.Instant;

import com.aprendendoSpring.course.entities.Produto;

public record ProdutoResponseDTO(
    Long idProduto,
    String nome,
    String categoria,
    Double preco,
    Instant dataDeValidade,
    String imgUrlProduto
) {

    public static ProdutoResponseDTO fromEntity(Produto produto) {
        return new ProdutoResponseDTO(
            produto.getIdProduto(),
            produto.getNome(),
            produto.getCategoria(),
            produto.getPreco(),
            produto.getDataDeValidade(),
            produto.getImgUrlProduto()
        );
    }
}