package com.maovares.ms_products.product.domain.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Order {
    private final String id;
    private final String customerName;
    private final String customerEmail;
    private final List<OrderItem> items;
    private final double total;
    private final Instant createdAt;

    public Order(String id, String customerName, String customerEmail, List<OrderItem> items, double total, Instant createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
    }

    @Data
    public static class OrderItem {
        private final String productId;
        private final String title;
        private final double price;
        private final int quantity;

        public OrderItem(String productId, String title, double price, int quantity) {
            this.productId = productId;
            this.title = title;
            this.price = price;
            this.quantity = quantity;
        }
    }
}