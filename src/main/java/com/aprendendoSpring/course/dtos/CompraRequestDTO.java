package com.aprendendoSpring.course.dtos;

import java.util.List;

public record CompraRequestDTO(
        Long fornecedorId,
        List<ItemCompraRequestDTO> itens
) {
}
