package com.aprendendoSpring.course.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_item_compra")
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_compra")
    private Long idItemCompra;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    public ItemCompra() {
    }

    public ItemCompra(Produto produto, Compra compra, Integer quantidade) {
        this.produto = produto;
        this.compra = compra;
        this.quantidade = quantidade;
        calcularSubTotal();
    }

    public double calcularSubTotal() {
        double valor = (produto == null || produto.getPreco() == null ? 0.0 : produto.getPreco()) * (quantidade == null ? 0 : quantidade);
        subtotal = valor;
        return valor;
    }

    public Long getIdItemCompra() {
        return idItemCompra;
    }

    public void setIdItemCompra(Long idItemCompra) {
        this.idItemCompra = idItemCompra;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
