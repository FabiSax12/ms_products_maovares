package com.maovares.ms_products.product.application.port.in;

import com.maovares.ms_products.product.domain.model.Product;

public interface GetProductQuery {
    Product execute(String id);
}
