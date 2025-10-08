package com.maovares.ms_products.order.infrastructure.web;

import com.maovares.ms_products.product.application.port.in.CreateOrderCommand;
import com.maovares.ms_products.product.domain.model.Order;
import com.maovares.ms_products.product.infraestructure.web.dto.CreateOrderDto;
import com.maovares.ms_products.product.infraestructure.web.dto.OrderResponseDto;
import com.maovares.ms_products.product.infraestructure.web.dto.mapper.OrderDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders", description = "API for orders management")
@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final CreateOrderCommand createOrderCommand;

    public OrderController(CreateOrderCommand createOrderCommand) {
        this.createOrderCommand = createOrderCommand;
    }


    // Endpoints

    @Operation(summary = "Create a order", description = "Creates a new order with the provided user and products details.")
    @ApiResponse(responseCode = "201", description = "Order created", content = @Content(schema = @Schema(implementation = OrderResponseDto.class)))
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid CreateOrderDto body) {
        log.info("Creating new order - Customer: {}, Email: {}, Products: {}",
                body.customerName(), body.customerEmail(), body.productsId().size());

        try {
            Order order = createOrderCommand.execute(body.customerName(), body.customerEmail(), body.productsId());

            log.info("Successfully created order: {} - Total: {}",
                    order.getId(), order.getTotal());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(OrderDtoMapper.toResponse(order));
        } catch (Exception e) {
            log.error("Error creating order for customer {}: {}", body.customerEmail(), e.getMessage(), e);
            throw e;
        }
    }

}
