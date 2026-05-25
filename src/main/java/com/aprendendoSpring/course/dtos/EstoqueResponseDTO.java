package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Estoque;

public record EstoqueResponseDTO(
        Long idEstoque,
        Long produtoId,
        String produtoNome,
        Integer quantidadeAtual,
        Integer quantidadeMaxima,
        String localizacao,
        String alerta
) {
    public static EstoqueResponseDTO fromEntity(Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getIdEstoque(),
                estoque.getProduto() == null ? null : estoque.getProduto().getIdProduto(),
                estoque.getProduto() == null ? null : estoque.getProduto().getNome(),
                estoque.getQuantidadeAtual(),
                estoque.getQuantidadeMaxima(),
                estoque.getLocalizacao(),
                estoque.alertarCapacidadeEstoque()
        );
    }
}
