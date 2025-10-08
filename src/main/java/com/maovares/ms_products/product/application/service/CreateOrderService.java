package com.maovares.ms_products.product.application.service;

import com.maovares.ms_products.product.application.port.in.CreateOrderCommand;
import com.maovares.ms_products.product.application.port.out.OrderRepository;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.model.Order;
import com.maovares.ms_products.product.domain.model.Product;
import com.maovares.ms_products.shared.application.port.out.QueueService;
import com.maovares.ms_products.shared.domain.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CreateOrderService implements CreateOrderCommand {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final QueueService queueService;

    public CreateOrderService(OrderRepository orderRepository, ProductRepository productRepository, QueueService queueService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.queueService = queueService;
    }

    @Override
    public Order execute(String customerName, String customerEmail, List<String> productIds) {
        log.info("Creating order - Customer: {}, Email: {}, Products: {}", customerName, customerEmail, productIds.size());

        try {
            // Fetch products and create order items
            List<Order.OrderItem> orderItems = new ArrayList<>();
            double total = 0.0;

            for (String productId : productIds) {
                log.debug("Fetching product with ID: {}", productId);
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

                Order.OrderItem orderItem = new Order.OrderItem(
                        product.getId(),
                        product.getTitle(),
                        product.getPrice(),
                        1 // Default quantity is 1
                );
                orderItems.add(orderItem);
                total += product.getPrice();
                log.debug("Added product to order - ID: {}, Title: {}, Price: {}", product.getId(), product.getTitle(), product.getPrice());
            }

            String orderId = UUID.randomUUID().toString();
            Instant createdAt = Instant.now();
            log.debug("Generated order ID: {}, Total: {}", orderId, total);

            Order order = new Order(orderId, customerName, customerEmail, orderItems, total, createdAt);

            log.info("Saving order to repository - ID: {}, Customer: {}", orderId, customerName);
            Order savedOrder = orderRepository.save(order);

            log.info("Order successfully created and saved - ID: {}, Total: {}", savedOrder.getId(), savedOrder.getTotal());

            // Send event to Azure Queue Storage
            List<Event.Item> eventItems = orderItems.stream()
                    .map(item -> new Event.Item(item.getProductId(), item.getQuantity()))
                    .toList();

            Event event = new Event(
                    savedOrder.getId(),
                    savedOrder.getCustomerEmail(),
                    savedOrder.getCustomerName(),
                    savedOrder.getTotal(),
                    eventItems,
                    savedOrder.getCreatedAt().toString()
            );

            log.info("Sending order event to Azure Queue - Order ID: {}", savedOrder.getId());
            queueService.sendMessage(event);

            return savedOrder;
        } catch (Exception e) {
            log.error("Error creating order for customer '{}': {}", customerEmail, e.getMessage(), e);
            throw e;
        }
    }
}
