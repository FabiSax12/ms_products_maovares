package com.maovares.ms_products.product.infraestructure.persistence.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "orders")
public class OrderDocument {
    @Id
    private String id;
    private String customerName;
    private String customerEmail;
    private List<OrderItemDocument> items;
    private double total;
    private Instant createdAt;

    public OrderDocument() {
    }

    public OrderDocument(String id, String customerName, String customerEmail, List<OrderItemDocument> items, double total, Instant createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
    }

    @Data
    public static class OrderItemDocument {
        private String productId;
        private String title;
        private double price;
        private int quantity;

        public OrderItemDocument() {
        }

        public OrderItemDocument(String productId, String title, double price, int quantity) {
            this.productId = productId;
            this.title = title;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
