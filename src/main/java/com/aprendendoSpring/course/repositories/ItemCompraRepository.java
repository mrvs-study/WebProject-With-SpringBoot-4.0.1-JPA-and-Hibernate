package com.aprendendoSpring.course.repositories;

import com.aprendendoSpring.course.entities.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {
}
