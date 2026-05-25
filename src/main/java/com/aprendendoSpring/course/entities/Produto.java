package com.aprendendoSpring.course.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double preco;

    @Column(name = "data_de_validade")
    private Instant dataDeValidade;

    @Column(name = "img_url_produto")
    private String imgUrlProduto;

    public Produto() {
    }

    public Produto(Long idProduto, String nome, String categoria, Double preco, Instant dataDeValidade,
            String imgUrlProduto) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.dataDeValidade = dataDeValidade;
        this.imgUrlProduto = imgUrlProduto;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }


    public Instant getDataDeValidade() {
        return dataDeValidade;
    }

    public void setDataDeValidade(Instant dataDeValidade) {
        this.dataDeValidade = dataDeValidade;
    }


    public String getImgUrlProduto() {
        return imgUrlProduto;
    }

    public void setImgUrlProduto(String imgUrlProduto) {
        this.imgUrlProduto = imgUrlProduto;
    }
}