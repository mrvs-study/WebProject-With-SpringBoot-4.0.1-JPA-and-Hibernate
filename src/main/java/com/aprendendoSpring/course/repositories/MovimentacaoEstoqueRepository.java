package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query(value = """
            SELECT *
            FROM tb_movimentacao_estoque
            WHERE tipo_movimentacao = :tipo
            ORDER BY data_movimentacao DESC
            """, nativeQuery = true)
    List<MovimentacaoEstoque> buscarPorTipoMovimentacao(@Param("tipo") String tipo);
}
