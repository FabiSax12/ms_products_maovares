package com.maovares.ms_products.shared.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class Event {
    private final String orderId;
    private final String customerEmail;
    private final String customerName;
    private final double total;
    private final List<Item> items;
    private final String createdAt;

    @Data
    public static class Item {
        private final String sku;
        private final int qty;
    }
}
