package com.maovares.ms_products.product.infraestructure.web.dto;

import com.maovares.ms_products.product.domain.model.Order;

import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
        String id,
        String customerName,
        String customerEmail,
        List<OrderItemDto> items,
        double total,
        Instant createdAt
) {
    public static OrderResponseDto fromDomain(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                items,
                order.getTotal(),
                order.getCreatedAt()
        );
    }

    public record OrderItemDto(
            String productId,
            String title,
            double price,
            int quantity
    ) {
    }
}
