package com.aprendendoSpring.course.dtos;

public record FornecedorRequestDTO(
        String razaoSocial,
        String cnpj,
        String telefone,
        String email,
        String cep
) {
}
