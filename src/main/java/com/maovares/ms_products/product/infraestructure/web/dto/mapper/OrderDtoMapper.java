package com.maovares.ms_products.product.infraestructure.web.dto.mapper;

import com.maovares.ms_products.product.domain.model.Order;
import com.maovares.ms_products.product.domain.model.Product;
import com.maovares.ms_products.product.infraestructure.web.dto.OrderResponseDto;
import com.maovares.ms_products.product.infraestructure.web.dto.ProductResponseDto;

public class OrderDtoMapper {
    public static OrderResponseDto toResponse(Order order) {
        return OrderResponseDto.fromDomain(order);
    }
}
