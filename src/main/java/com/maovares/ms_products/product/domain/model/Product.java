package com.maovares.ms_products.product.domain.model;

import lombok.Data;

@Data
public class Product {
    private final String id;
    private final double price;
    private final String description;
    private final String image;
    private final String title;

    public Product(String id, Double price, String description, String image, String title) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.image = image;
        this.title = title;
    }
}
