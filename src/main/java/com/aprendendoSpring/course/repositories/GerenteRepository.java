package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
}
