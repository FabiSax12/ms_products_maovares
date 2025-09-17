package com.maovares.ms_products.product.infraestructure.web;

import java.util.List;

import com.maovares.ms_products.product.application.port.in.GetProductByIdQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maovares.ms_products.product.application.port.in.GetProductsQuery;
import com.maovares.ms_products.product.domain.model.Product;
import com.maovares.ms_products.product.infraestructure.web.dto.PagedResponseDto;
import com.maovares.ms_products.product.infraestructure.web.dto.ProductResponseDto;
import com.maovares.ms_products.product.infraestructure.web.dto.mapper.ProductDtoMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Products", description = "API for product management")
@RestController
@RequestMapping("/products")
public class ProductController {
        private final GetProductsQuery getProductsQuery;
        private final GetProductByIdQuery getProductByIdQuery;

        public ProductController(GetProductsQuery getProductsQuery,  GetProductByIdQuery getProductByIdQuery) {
                this.getProductsQuery = getProductsQuery;
                this.getProductByIdQuery = getProductByIdQuery;
        }

        @Operation(summary = "Get product by id", description = "")
        @ApiResponse(responseCode = "200", description = "Product by id", content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
        @ApiResponse(responseCode = "400", description = "Invalid params")
        @GetMapping
        public PagedResponseDto<ProductResponseDto> getProducts(Pageable pageable) {

                Page<Product> products = getProductsQuery.execute(pageable);

                List<ProductResponseDto> content = products.getContent()
                                .stream()
                                .map(ProductDtoMapper::toResponse)
                                .toList();

                return new PagedResponseDto<>(
                                content,
                                products.getNumber(),
                                products.getSize(),
                                products.getTotalElements(),
                                products.getTotalPages());
        }

    @Operation(summary = "Get paginated product lists", description = "Returns a paginated product lists, includes optional params like: page and size.")
    @ApiResponse(responseCode = "200", description = "Paginated product list", content = @Content(schema = @Schema(implementation = PagedResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid params")
    @GetMapping(path = "/{id}")
    public ProductResponseDto productById(@PathVariable String id) {
        Product product = getProductByIdQuery.execute(id);

        return ProductDtoMapper.toResponse(product);
    }

}
