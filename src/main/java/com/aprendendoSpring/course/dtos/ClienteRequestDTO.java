package com.aprendendoSpring.course.dtos;

public record ClienteRequestDTO(
        String nome,
        String cpf,
        String email,
        String telefone,
        String senha
) {
}
