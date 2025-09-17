package com.maovares.ms_products.product.application.port.in;

import com.maovares.ms_products.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetProductByIdQuery {
    Product execute(String id);
}
