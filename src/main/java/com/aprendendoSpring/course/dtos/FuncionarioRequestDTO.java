package com.aprendendoSpring.course.dtos;

import java.time.Instant;

import com.aprendendoSpring.course.entities.enums.CargoFuncionario;

public record FuncionarioRequestDTO(
        String nome,
        String cpf,
        String email,
        String telefone,
        String senha,
        CargoFuncionario cargo,
        Double salario,
        Instant dataDeAdmissao
) {
}