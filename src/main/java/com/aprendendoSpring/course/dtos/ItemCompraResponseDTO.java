package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.ItemCompra;

public record ItemCompraResponseDTO(
        Long idItemCompra,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        Double subtotal
) {
    public static ItemCompraResponseDTO fromEntity(ItemCompra item) {
        return new ItemCompraResponseDTO(
                item.getIdItemCompra(),
                item.getProduto() == null ? null : item.getProduto().getIdProduto(),
                item.getProduto() == null ? null : item.getProduto().getNome(),
                item.getQuantidade(),
                item.getSubtotal()
        );
    }
}
