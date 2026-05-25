package com.aprendendoSpring.course.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aprendendoSpring.course.entities.enums.TipoMovimentacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Long idMovimentacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false)
    private TipoMovimentacao tipoMovimentacao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "data_movimentacao", nullable = false)
    private Instant dataMovimentacao;

    @ManyToOne
    @JoinColumn(name = "id_estoque", nullable = false)
    private Estoque estoque;

    public MovimentacaoEstoque() {
        this.dataMovimentacao = Instant.now();
    }

    public void registrarEntrada() {
        tipoMovimentacao = TipoMovimentacao.ENTRADA;
    }

    public void registrarSaida() {
        tipoMovimentacao = TipoMovimentacao.SAIDA;
    }

    public List<MovimentacaoEstoque> gerarHistorico() {
        List<MovimentacaoEstoque> historico = new ArrayList<>();
        historico.add(this);
        return historico;
    }

    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Instant getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(Instant dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }
}
