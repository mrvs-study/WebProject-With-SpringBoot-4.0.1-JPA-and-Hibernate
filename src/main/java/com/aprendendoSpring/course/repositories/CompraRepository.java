package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query(value = """
            SELECT *
            FROM tb_compra
            WHERE id_fornecedor = :fornecedorId
            ORDER BY momento DESC
            """, nativeQuery = true)
    List<Compra> buscarComprasPorFornecedor(@Param("fornecedorId") Long fornecedorId);
}
