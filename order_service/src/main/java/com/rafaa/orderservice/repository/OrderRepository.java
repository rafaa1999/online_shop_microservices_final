package com.rafaa.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaa.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
