package com.aprendendoSpring.course.dtos;

public record ItemCompraRequestDTO(
        Long produtoId,
        Integer quantidade
) {
}
