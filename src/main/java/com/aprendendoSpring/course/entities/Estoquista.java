package com.aprendendoSpring.course.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estoquista")
@PrimaryKeyJoinColumn(name = "id_funcionario")
public class Estoquista extends Funcionario {

    public Estoquista() {
    }

    public MovimentacaoEstoque realizarMovimentacao() {
        return new MovimentacaoEstoque();
    }

    public String localizarProduto() {
        return "Localização consultada pelo estoque.";
    }

    public Produto registrarProduto() {
        return new Produto();
    }
}
