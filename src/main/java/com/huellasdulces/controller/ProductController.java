package com.huellasdulces.controller;

import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.dto.StockRequest;
import com.huellasdulces.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ProductResponse> searchProducts(
            @RequestParam("q") String query) {
        return productService.searchProducts(query);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<String> getProductCategories() {
        return productService.getProductCategories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse withdrawStock(
            @PathVariable Long id,
            @Valid @RequestBody StockRequest stockRequest) {
        return productService.withdrawStock(id, stockRequest);
    }
}