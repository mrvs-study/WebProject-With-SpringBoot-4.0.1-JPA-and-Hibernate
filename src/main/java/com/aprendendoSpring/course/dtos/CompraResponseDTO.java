package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Compra;
import com.aprendendoSpring.course.entities.enums.StatusCompra;

import java.time.Instant;
import java.util.List;

public record CompraResponseDTO(
        Long idCompra,
        Long fornecedorId,
        String fornecedorRazaoSocial,
        Instant momento,
        Double total,
        StatusCompra status,
        List<ItemCompraResponseDTO> itens
) {
    public static CompraResponseDTO fromEntity(Compra compra) {
        return new CompraResponseDTO(
                compra.getIdCompra(),
                compra.getFornecedor() == null ? null : compra.getFornecedor().getIdFornecedor(),
                compra.getFornecedor() == null ? null : compra.getFornecedor().getRazaoSocial(),
                compra.getMomento(),
                compra.getTotal(),
                compra.getStatus(),
                compra.getItens().stream().map(ItemCompraResponseDTO::fromEntity).toList()
        );
    }
}
