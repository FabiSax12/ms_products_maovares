package com.maovares.ms_products.product.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maovares.ms_products.product.domain.model.Product;

public interface ProductRepository {
    Page<Product> findAll(Pageable pageable);
    Product findProductById(String id);
    // count??
}
