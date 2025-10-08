package com.maovares.ms_products.product.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.maovares.ms_products.product.application.port.in.CreateProductCommand;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.model.Product;
import com.maovares.ms_products.shared.application.port.out.QueueService;
import com.maovares.ms_products.shared.domain.model.Event;

@Service
public class CreateProductService implements CreateProductCommand {

    private final ProductRepository productRepository;
    private final QueueService queueService;

    public CreateProductService(ProductRepository productRepository, QueueService queueService) {
        this.productRepository = productRepository;
        this.queueService = queueService;
    }

    @Override
    public Product execute(String description, double price, String image, String title) {
        String id = UUID.randomUUID().toString();

        Product product = new Product(id, price, description, image, title);
        Product savedProduct = productRepository.save(product);

        // Send event to Azure Queue Storage
        Event.Item item = new Event.Item(savedProduct.getId(), 1);
        Event event = new Event(
                UUID.randomUUID().toString(),
                "vargasarayafabian11@gmail.com",
                "Fabián Vargas",
                savedProduct.getPrice(),
                List.of(item),
                Instant.now().toString()
        );

        queueService.sendMessage(event);

        return savedProduct;
    }

}
