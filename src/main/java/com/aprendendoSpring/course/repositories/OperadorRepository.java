package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Operador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperadorRepository extends JpaRepository<Operador, Long> {
}
