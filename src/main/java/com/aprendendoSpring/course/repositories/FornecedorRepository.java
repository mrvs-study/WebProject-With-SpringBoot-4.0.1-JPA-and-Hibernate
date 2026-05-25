package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
