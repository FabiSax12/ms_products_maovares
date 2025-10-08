package com.maovares.ms_products.product.infraestructure.web.dto;

import com.maovares.ms_products.product.domain.model.Product;

public record ProductResponseDto(String id, String description, double price, String image, String title) {
    public static ProductResponseDto fromDomain(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                product.getImage(),
                product.getTitle());
    }
}
