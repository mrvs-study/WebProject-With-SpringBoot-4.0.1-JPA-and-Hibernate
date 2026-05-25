package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query(value = """
            SELECT *
            FROM tb_pagamento
            WHERE status = 'PENDENTE'
            ORDER BY momento ASC
            """, nativeQuery = true)
    List<Pagamento> buscarPagamentosPendentes();
}
