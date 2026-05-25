package com.aprendendoSpring.course.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_gerente")
@PrimaryKeyJoinColumn(name = "id_funcionario")
public class Gerente extends Funcionario {

    public Gerente() {
    }

    public String gerarRelatorio() {
        return "Relatório gerado.";
    }

    public Funcionario cadastrarFuncionario() {
        return new Funcionario();
    }

    public Funcionario removerFuncionario() {
        return new Funcionario();
    }

    public Funcionario atualizarFuncionario() {
        return new Funcionario();
    }

    public Funcionario listarFuncionario() {
        return new Funcionario();
    }
}
