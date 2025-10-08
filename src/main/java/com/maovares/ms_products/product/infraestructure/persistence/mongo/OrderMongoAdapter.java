package com.maovares.ms_products.product.infraestructure.persistence.mongo;

import com.maovares.ms_products.product.application.port.out.OrderRepository;
import com.maovares.ms_products.product.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class OrderMongoAdapter implements OrderRepository {

    private final MongoTemplate mongoTemplate;

    public OrderMongoAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        log.info("Executing MongoDB query to find all orders with pagination - Page: {}, Size: {}",
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
            List<OrderDocument> orderDocuments = mongoTemplate.find(query, OrderDocument.class);

            log.debug("Found {} order documents, converting to domain objects", orderDocuments.size());
            List<Order> orders = orderDocuments.stream()
                    .map(this::toDomain)
                    .toList();

            log.debug("Counting total orders for pagination");
            long totalOrders = mongoTemplate.count(new Query(), OrderDocument.class);

            log.info("MongoDB query completed - Found {} orders out of {} total",
                    orders.size(), totalOrders);

            return new PageImpl<>(orders, pageable, totalOrders);
        } catch (Exception e) {
            log.error("Error executing MongoDB findAll query: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Order> findById(String id) {
        log.info("Executing MongoDB query to find order by ID: {}", id);

        try {
            log.debug("Building MongoDB query with criteria: id = {}", id);
            Query query = new Query(Criteria.where("id").is(id));

            log.debug("Executing findOne query on MongoDB collection");
            OrderDocument orderDocument = mongoTemplate.findOne(query, OrderDocument.class);

            if (orderDocument != null) {
                log.info("Order found in MongoDB - ID: {}, Customer: {}",
                        orderDocument.getId(), orderDocument.getCustomerName());
            } else {
                log.info("Order not found in MongoDB - ID: {}", id);
            }

            return Optional.ofNullable(orderDocument)
                    .map(this::toDomain);
        } catch (Exception e) {
            log.error("Error executing MongoDB findById query for ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Order save(Order order) {
        log.info("Executing MongoDB save operation for order - ID: {}, Customer: {}",
                order.getId(), order.getCustomerName());

        try {
            log.debug("Converting domain Order to OrderDocument");
            OrderDocument orderDocument = toDocument(order);

            log.debug("Saving OrderDocument to MongoDB collection");
            OrderDocument savedDocument = mongoTemplate.save(orderDocument);

            log.info("Order successfully saved to MongoDB - ID: {}, Customer: {}",
                    savedDocument.getId(), savedDocument.getCustomerName());

            return toDomain(savedDocument);
        } catch (Exception e) {
            log.error("Error saving order to MongoDB - ID: {}, Customer: {}, Error: {}",
                    order.getId(), order.getCustomerName(), e.getMessage(), e);
            throw e;
        }
    }

    private Order toDomain(OrderDocument document) {
        List<Order.OrderItem> items = document.getItems().stream()
                .map(item -> new Order.OrderItem(
                        item.getProductId(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new Order(
                document.getId(),
                document.getCustomerName(),
                document.getCustomerEmail(),
                items,
                document.getTotal(),
                document.getCreatedAt()
        );
    }

    private OrderDocument toDocument(Order order) {
        List<OrderDocument.OrderItemDocument> items = order.getItems().stream()
                .map(item -> new OrderDocument.OrderItemDocument(
                        item.getProductId(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderDocument(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                items,
                order.getTotal(),
                order.getCreatedAt()
        );
    }
}
