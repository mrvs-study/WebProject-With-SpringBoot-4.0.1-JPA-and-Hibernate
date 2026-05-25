package com.aprendendoSpring.course.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tb_estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Long idEstoque;

    @OneToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @Column(name = "quantidade_maxima", nullable = false)
    private Integer quantidadeMaxima;

    @Column(nullable = false)
    private String localizacao;

    public Estoque() {
    }

    public Estoque(Long idEstoque, Produto produto, Integer quantidadeAtual, Integer quantidadeMaxima, String localizacao) {
        this.idEstoque = idEstoque;
        this.produto = produto;
        this.quantidadeAtual = quantidadeAtual;
        this.quantidadeMaxima = quantidadeMaxima;
        this.localizacao = localizacao;
    }

    public int verificarEstoque() {
        return quantidadeAtual == null ? 0 : quantidadeAtual;
    }

    public String alertarCapacidadeEstoque() {
        if (quantidadeAtual != null && quantidadeMaxima != null && quantidadeAtual > quantidadeMaxima) {
            return "Estoque acima da capacidade máxima.";
        }
        if (quantidadeAtual != null && quantidadeAtual <= 0) {
            return "Produto sem estoque.";
        }
        return "Estoque dentro da capacidade.";
    }

    public Produto alertaVencimento() {
        if (produto != null && produto.getDataDeValidade() != null && produto.getDataDeValidade().isBefore(Instant.now())) {
            return produto;
        }
        return null;
    }

    public void atualizarQuantidadeAtual() {
        if (quantidadeAtual == null) {
            quantidadeAtual = 0;
        }
    }

    public int adicionarQuantidade() {
        if (quantidadeAtual == null) {
            quantidadeAtual = 0;
        }
        quantidadeAtual++;
        return quantidadeAtual;
    }

    public void removerQuantidade() {
        if (quantidadeAtual == null || quantidadeAtual <= 0) {
            quantidadeAtual = 0;
            return;
        }
        quantidadeAtual--;
    }

    public Long getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(Long idEstoque) {
        this.idEstoque = idEstoque;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Integer getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(Integer quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
