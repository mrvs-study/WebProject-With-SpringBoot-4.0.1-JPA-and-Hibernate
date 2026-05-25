package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.ItemVenda;

public record ItemVendaResponseDTO(
        Long idItemVenda,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        Double precoUnitario,
        Double subtotal
) {
    public static ItemVendaResponseDTO fromEntity(ItemVenda item) {
        return new ItemVendaResponseDTO(
                item.getIdItemVenda(),
                item.getProduto() == null ? null : item.getProduto().getIdProduto(),
                item.getProduto() == null ? null : item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal()
        );
    }
}
