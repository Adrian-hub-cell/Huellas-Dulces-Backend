package com.huellasdulces.service;

import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.dto.StockRequest;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> searchProducts(String query);

    List<String> getProductCategories();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    ProductResponse withdrawStock(Long id, StockRequest stockRequest);
}