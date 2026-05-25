package com.aprendendoSpring.course.entities;

import jakarta.persistence.*;
import java.time.Instant;

import com.aprendendoSpring.course.entities.enums.CargoFuncionario;

@Entity
@Table(name = "tb_funcionario")
@Inheritance(strategy = InheritanceType.JOINED)
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    private Long idFuncionario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoFuncionario cargo;
    
    @Column(nullable = false)
    private Double salario;

    @Column(name = "data_de_admissao", nullable = false)
    private Instant dataDeAdmissao;

    public Funcionario() {
    }

    public Funcionario(Long idFuncionario, String nome, String cpf, String email, String telefone, String senha, CargoFuncionario cargo, Double salario, Instant dataDeAdmissao) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.cargo = cargo;
        this.salario = salario;
        this.dataDeAdmissao = dataDeAdmissao;
    }

    public void alterarCadastro() {
        // Método do diagrama: a alteração é feita pela camada Service.
    }

    public boolean autenticar() {
        return email != null && !email.isBlank() && senha != null && !senha.isBlank();
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public CargoFuncionario getCargo() {
        return cargo;
    }

    public void setCargo(CargoFuncionario cargo) {
        this.cargo = cargo;
    }
    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public Instant getDataDeAdmissao() {
        return dataDeAdmissao;
    }

    public void setDataDeAdmissao(Instant dataDeAdmissao) {
        this.dataDeAdmissao = dataDeAdmissao;
    }
}
