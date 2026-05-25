package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.Estoquista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoquistaRepository extends JpaRepository<Estoquista, Long> {
}
