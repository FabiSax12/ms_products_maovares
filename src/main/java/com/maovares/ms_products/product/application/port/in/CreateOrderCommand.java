package com.maovares.ms_products.product.application.port.in;

import com.maovares.ms_products.product.domain.model.Order;

import java.util.List;

public interface CreateOrderCommand {
    Order execute(String customerName, String customerEmail, List<String> productIds);
}
