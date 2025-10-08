package com.maovares.ms_products.product.infraestructure.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "products")
public class ProductDocument {
    @Id
    private String id;
    private String description;
    private double price;
    private String title;
    private String image;

    public ProductDocument() {
    }

    public ProductDocument(String id, String description, double price, String title, String image) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.title = title;
        this.image = image;
    }

}
