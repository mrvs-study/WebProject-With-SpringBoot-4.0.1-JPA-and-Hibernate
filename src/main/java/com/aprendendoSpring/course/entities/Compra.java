package com.aprendendoSpring.course.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aprendendoSpring.course.entities.enums.StatusCompra;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;

    @Column(nullable = false)
    private Instant momento;

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCompra status;

    @ManyToOne
    @JoinColumn(name = "id_fornecedor", nullable = false)
    private Fornecedor fornecedor;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCompra> itens = new ArrayList<>();

    public Compra() {
        this.momento = Instant.now();
        this.status = StatusCompra.PENDENTE;
        this.total = 0.0;
    }
    public void registrarCompra() {
        this.status = StatusCompra.RECEBIDA;
        calcularTotal();
    }

    public double calcularTotal() {
        this.total = itens.stream()
                .mapToDouble(ItemCompra::calcularSubTotal)
                .sum();

        return this.total;
    }

    public void atualizarStatus() {
        if (this.total != null && this.total > 0) {
            this.status = StatusCompra.RECEBIDA;
        }
    }

    public String verificarStatus() {
        return this.status.name();
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
    }

    public Instant getMomento() {
        return momento;
    }

    public void setMomento(Instant momento) {
        this.momento = momento;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    public StatusCompra getStatus() {
        return status;
    }

    public void setStatus(StatusCompra status) {
        this.status = status;
    }
    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<ItemCompra> getItens() {
        return itens;
    }
}
