package com.maovares.ms_products.product.infraestructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotNull List<String> productsId
) {
}
