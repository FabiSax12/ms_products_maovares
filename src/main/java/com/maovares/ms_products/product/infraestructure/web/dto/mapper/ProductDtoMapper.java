package com.maovares.ms_products.product.infraestructure.web.dto.mapper;

import com.maovares.ms_products.product.domain.model.Product;
import com.maovares.ms_products.product.infraestructure.web.dto.ProductResponseDto;

public class ProductDtoMapper {
    public static ProductResponseDto toResponse(Product product) {
        return ProductResponseDto.fromDomain(product);
    }
}
