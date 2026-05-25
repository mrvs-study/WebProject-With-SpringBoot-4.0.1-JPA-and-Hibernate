package com.aprendendoSpring.course.dtos;

import java.time.Instant;

import com.aprendendoSpring.course.entities.Funcionario;
import com.aprendendoSpring.course.entities.enums.CargoFuncionario;

public record FuncionarioResponseDTO(
        Long idFuncionario,
        String nome,
        String cpf,
        String email,
        String telefone,
        CargoFuncionario cargo,
        Double salario,
        Instant dataDeAdmissao
) {
    public static FuncionarioResponseDTO fromEntity(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
                funcionario.getIdFuncionario(),
                funcionario.getNome(),
                funcionario.getCpf(),
                funcionario.getEmail(),
                funcionario.getTelefone(),
                funcionario.getCargo(),
                funcionario.getSalario(),
                funcionario.getDataDeAdmissao()
        );
    }
}