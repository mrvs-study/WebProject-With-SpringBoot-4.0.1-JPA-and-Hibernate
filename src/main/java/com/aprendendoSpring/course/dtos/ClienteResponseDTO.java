package com.aprendendoSpring.course.dtos;

import com.aprendendoSpring.course.entities.Cliente;

public record ClienteResponseDTO(
        Long idCliente,
        String nome,
        String cpf,
        String email,
        String telefone
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }
}
