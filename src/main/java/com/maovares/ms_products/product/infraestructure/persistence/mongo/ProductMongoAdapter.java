package com.maovares.ms_products.product.infraestructure.persistence.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.maovares.ms_products.product.application.port.out.ProductRepository;
import com.maovares.ms_products.product.domain.model.Product;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ProductMongoAdapter implements ProductRepository {

        private final MongoTemplate mongoTemplate;

        public ProductMongoAdapter(MongoTemplate mongoTemplate) {
                this.mongoTemplate = mongoTemplate;
        }

        @Override
        public Page<Product> findAll(Pageable pageable) {
                log.info("Executing MongoDB query to find all products with pagination - Page: {}, Size: {}", 
                        pageable.getPageNumber(), pageable.getPageSize());
                
                try {
                        long pageSize = pageable.getPageSize();
                        long pageNumber = pageable.getPageNumber();
                        long skip = pageNumber * pageSize;

                        log.debug("Building MongoDB query with skip: {}, limit: {}", skip, pageSize);
                        Query query = new Query()
                                        .with(pageable)
                                        .skip(skip)
                                        .limit((int) pageSize);

                        log.debug("Executing find query on MongoDB collection");
                        List<ProductDocument> productDocuments = mongoTemplate.find(query, ProductDocument.class);
                        
                        log.debug("Found {} product documents, converting to domain objects", productDocuments.size());
                        List<Product> products = productDocuments.stream()
                                        .map(doc -> new Product(doc.getId(), doc.getPrice(), doc.getDescription(),
                                                        doc.getImage(),
                                                        doc.getTitle()))
                                        .toList();

                        log.debug("Counting total products for pagination");
                        long totalProducts = mongoTemplate.count(new Query(), ProductDocument.class);

                        log.info("MongoDB query completed - Found {} products out of {} total", 
                                products.size(), totalProducts);

                        return new PageImpl<>(products, pageable, totalProducts);
                } catch (Exception e) {
                        log.error("Error executing MongoDB findAll query: {}", e.getMessage(), e);
                        throw e;
                }
        }

        @Override
        public Optional<Product> findById(String id) {
                log.info("Executing MongoDB query to find product by ID: {}", id);
                
                try {
                        log.debug("Building MongoDB query with criteria: id = {}", id);
                        Query query = new Query(Criteria.where("id").is(id));
                        
                        log.debug("Executing findOne query on MongoDB collection");
                        ProductDocument productDocument = mongoTemplate.findOne(query, ProductDocument.class);
                        
                        if (productDocument != null) {
                                log.info("Product found in MongoDB - ID: {}, Title: {}", 
                                        productDocument.getId(), productDocument.getTitle());
                        } else {
                                log.info("Product not found in MongoDB - ID: {}", id);
                        }
                        
                        return Optional.ofNullable(productDocument)
                                        .map(doc -> new Product(doc.getId(), doc.getPrice(), doc.getDescription(),
                                                        doc.getImage(),
                                                        doc.getTitle()));
                } catch (Exception e) {
                        log.error("Error executing MongoDB findById query for ID {}: {}", id, e.getMessage(), e);
                        throw e;
                }
        }

        @Override
        public Product save(Product product) {
                log.info("Executing MongoDB save operation for product - ID: {}, Title: {}", 
                        product.getId(), product.getTitle());
                
                try {
                        log.debug("Converting domain Product to ProductDocument");
                        ProductDocument productDocument = new ProductDocument(product.getId(), product.getDescription(),
                                        product.getPrice(),
                                        product.getImage(), product.getTitle());
                        
                        log.debug("Saving ProductDocument to MongoDB collection");
                        ProductDocument savedDocument = mongoTemplate.save(productDocument);
                        
                        log.info("Product successfully saved to MongoDB - ID: {}, Title: {}", 
                                savedDocument.getId(), savedDocument.getTitle());
                        
                        return new Product(savedDocument.getId(), savedDocument.getPrice(), savedDocument.getDescription(),
                                        savedDocument.getImage(), savedDocument.getTitle());
                } catch (Exception e) {
                        log.error("Error saving product to MongoDB - ID: {}, Title: {}, Error: {}", 
                                product.getId(), product.getTitle(), e.getMessage(), e);
                        throw e;
                }
        }
}
