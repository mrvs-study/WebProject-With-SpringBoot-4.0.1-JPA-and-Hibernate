package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Venda;
import com.aprendendoSpring.course.entities.enums.StatusVenda;

import java.time.Instant;
import java.util.List;

public record VendaResponseDTO(
        Long idVenda,
        Long clienteId,
        String clienteNome,
        Instant momento,
        StatusVenda statusVenda,
        Double total,
        List<ItemVendaResponseDTO> itens
) {
    public static VendaResponseDTO fromEntity(Venda venda) {
        return new VendaResponseDTO(
                venda.getIdVenda(),
                venda.getCliente() == null ? null : venda.getCliente().getIdCliente(),
                venda.getCliente() == null ? null : venda.getCliente().getNome(),
                venda.getMomento(),
                venda.getStatusVenda(),
                venda.getTotal(),
                venda.getItens().stream()
                        .map(ItemVendaResponseDTO::fromEntity)
                        .toList()
        );
    }
}
