package com.aprendendoSpring.course.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aprendendoSpring.course.entities.enums.StatusVenda;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private Long idVenda;

    @Column(nullable = false)
    private Instant momento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_venda", nullable = false)
    private StatusVenda statusVenda;

    @Column(nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = true)
    private Cliente cliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToOne(mappedBy = "venda", cascade = CascadeType.ALL)
    private Pagamento pagamento;

    public Venda() {
        this.momento = Instant.now();
        this.statusVenda = StatusVenda.ABERTA;
        this.total = 0.0;
    }

    public double calcularTotal() {
        total = itens.stream()
                .mapToDouble(ItemVenda::calcularSubTotal)
                .sum();
        return total;
    }

    public void cancelarVenda() {
        this.statusVenda = StatusVenda.CANCELADA;
    }

    public void finalizarVenda() {
        this.statusVenda = StatusVenda.FINALIZADA;
        calcularTotal();
    }

    public void atualizarStatus() {
        if (pagamento != null && pagamento.verificarPagamento()) {
            this.statusVenda = StatusVenda.FINALIZADA;
        }
    }

    public String visualizarStatus() {
        return this.statusVenda.name();
    }

    public void visualizarVenda() {
        // Método do diagrama: a visualização é feita via endpoint GET.
    }

    public Long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    public Instant getMomento() {
        return momento;
    }

    public void setMomento(Instant momento) {
        this.momento = momento;
    }

    public StatusVenda getStatusVenda() {
        return statusVenda;
    }

    public void setStatusVenda(StatusVenda statusVenda) {
        this.statusVenda = statusVenda;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }
}
