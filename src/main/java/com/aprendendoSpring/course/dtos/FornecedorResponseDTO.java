package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Fornecedor;

public record FornecedorResponseDTO(
        Long idFornecedor,
        String razaoSocial,
        String cnpj,
        String telefone,
        String email,
        String cep
) {
    public static FornecedorResponseDTO fromEntity(Fornecedor fornecedor) {
        return new FornecedorResponseDTO(
                fornecedor.getIdFornecedor(),
                fornecedor.getRazaoSocial(),
                fornecedor.getCnpj(),
                fornecedor.getTelefone(),
                fornecedor.getEmail(),
                fornecedor.getCep()
        );
    }
}
