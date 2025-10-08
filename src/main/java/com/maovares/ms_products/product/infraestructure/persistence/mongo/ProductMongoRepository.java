package com.maovares.ms_products.product.infraestructure.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {
}
