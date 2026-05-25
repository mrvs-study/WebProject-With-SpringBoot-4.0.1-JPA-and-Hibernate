package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Optional<Funcionario> findByEmail(String email);
}
