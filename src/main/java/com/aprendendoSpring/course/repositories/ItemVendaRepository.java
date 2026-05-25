package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
}
