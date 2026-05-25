package com.aprendendoSpring.course.dtos;

public record ItemVendaRequestDTO(
        Long produtoId,
        Integer quantidade
) {
}
