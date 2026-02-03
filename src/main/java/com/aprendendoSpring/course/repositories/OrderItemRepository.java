package com.aprendendoSpring.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aprendendoSpring.course.entities.OrderItem;
import com.aprendendoSpring.course.entities.pk.OrderItemPk;

public interface  OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk>{

}
