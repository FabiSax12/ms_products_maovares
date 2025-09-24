package com.maovares.ms_products.product.application.service;

import org.springframework.stereotype.Service;

import com.maovares.ms_products.product.application.port.in.GetProductQuery;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.exception.ProductNotFoundException;
import com.maovares.ms_products.product.domain.model.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetProductService implements GetProductQuery {

    private final ProductRepository productRepository;

    public GetProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(String id) {
        log.info("Getting product by ID: {}", id);
        
        try {
            log.debug("Searching for product in repository - ID: {}", id);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));
            
            log.info("Product found successfully - ID: {}, Title: {}", product.getId(), product.getTitle());
            return product;
        } catch (ProductNotFoundException e) {
            log.warn("Product not found - ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
