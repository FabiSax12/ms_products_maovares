package com.maovares.ms_products.product.application.service;

import com.maovares.ms_products.product.application.port.in.GetProductByIdQuery;
import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class GetProductByIdService implements GetProductByIdQuery {

    private final ProductRepository productRepository;

    public GetProductByIdService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(String id) {
        return productRepository.findProductById(id);
    }
}
