package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query(value = """
            SELECT *
            FROM tb_venda
            WHERE status_venda = :status
            ORDER BY momento DESC
            """, nativeQuery = true)
    List<Venda> buscarVendasPorStatus(@Param("status") String status);
}
