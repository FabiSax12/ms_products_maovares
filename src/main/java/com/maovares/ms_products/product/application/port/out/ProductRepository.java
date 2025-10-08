package com.maovares.ms_products.product.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maovares.ms_products.product.domain.model.Product;

public interface ProductRepository {
    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(String id);

    Product save(Product product);
}
