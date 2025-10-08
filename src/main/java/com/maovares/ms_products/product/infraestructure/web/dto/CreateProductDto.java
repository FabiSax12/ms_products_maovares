package com.maovares.ms_products.product.infraestructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductDto(@NotBlank String description, @Min(0) double price, @NotNull String image,
        @NotBlank String title) {
}
