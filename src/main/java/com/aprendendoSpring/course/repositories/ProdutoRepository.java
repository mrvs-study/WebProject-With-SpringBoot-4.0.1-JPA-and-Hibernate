package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    @Query(value = """
            SELECT *
            FROM tb_produto
            WHERE data_de_validade IS NOT NULL
              AND data_de_validade <= :limite
            ORDER BY data_de_validade ASC
            """, nativeQuery = true)
    List<Produto> buscarProdutosProximosDoVencimento(@Param("limite") Instant limite);
}
