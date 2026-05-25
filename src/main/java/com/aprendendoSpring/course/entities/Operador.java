package com.aprendendoSpring.course.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_operador")
@PrimaryKeyJoinColumn(name = "id_funcionario")
public class Operador extends Funcionario {

    public Operador() {
    }

    public Venda registrarVenda() {
        return new Venda();
    }

    public Pagamento registrarPagamento() {
        return new Pagamento();
    }

    public void cancelarVenda() {
        // Método do diagrama: a venda é cancelada na camada Service.
    }

    public String emitirNota() {
        return "Nota emitida.";
    }
}
