package com.aprendendoSpring.course.dtos;

import java.util.List;

public record VendaRequestDTO(
        Long clienteId,
        List<ItemVendaRequestDTO> itens
) {
}
