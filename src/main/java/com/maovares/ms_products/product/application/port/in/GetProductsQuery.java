package com.maovares.ms_products.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maovares.ms_products.product.domain.model.Product;

public interface GetProductsQuery {
    Page<Product> execute(Pageable pageable);
}
