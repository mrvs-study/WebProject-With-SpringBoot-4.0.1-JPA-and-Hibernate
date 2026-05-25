package com.aprendendoSpring.course.entities;

import java.time.Instant;

import com.aprendendoSpring.course.entities.enums.FormaPagamento;
import com.aprendendoSpring.course.entities.enums.StatusPagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long idPagamento;

    @Column(nullable = false)
    private Instant momento;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @OneToOne
    @JoinColumn(name = "id_venda", nullable = false, unique = true)
    private Venda venda;

    public Pagamento() {
        this.momento = Instant.now();
        this.status = StatusPagamento.PENDENTE;
    }

    public boolean processarPagamento() {
        if (valor == null || valor <= 0) {
            this.status = StatusPagamento.RECUSADO;
            return false;
        }

        this.status = StatusPagamento.APROVADO;
        return true;
    }
    public boolean verificarPagamento() {
        return this.status == StatusPagamento.APROVADO;
    }

    public String verificarStatus() {
        return this.status.name();
    }

    public Long getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }

    public Instant getMomento() {
        return momento;
    }

    public void setMomento(Instant momento) {
        this.momento = momento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
}
