package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProduto_IdProduto(Long idProduto);

    @Query(value = """
            SELECT *
            FROM tb_estoque
            WHERE quantidade_atual <= 0
            ORDER BY quantidade_atual ASC
            """, nativeQuery = true)
    List<Estoque> buscarProdutosSemEstoque();
}
