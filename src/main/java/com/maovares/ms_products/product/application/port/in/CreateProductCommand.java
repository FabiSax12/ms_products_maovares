package com.maovares.ms_products.product.application.port.in;

import com.maovares.ms_products.product.domain.model.Product;

public interface CreateProductCommand {
    Product execute(String description, double price, String image, String title);
}
