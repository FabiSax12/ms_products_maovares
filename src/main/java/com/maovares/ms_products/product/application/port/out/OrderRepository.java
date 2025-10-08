package com.maovares.ms_products.product.application.port.out;

import com.maovares.ms_products.product.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository {
    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(String id);

    Order save(Order order);
}
