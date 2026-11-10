
package com.huellasdulces.service;

import com.huellasdulces.document.ProductAuditDocument;
import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.dto.StockRequest;
import com.huellasdulces.exception.ProductNotFoundException;
import com.huellasdulces.exception.StockConflictException;
import com.huellasdulces.mapper.ProductMapper;
import com.huellasdulces.model.Product;
import com.huellasdulces.repository.ProductAuditRepository;
import com.huellasdulces.repository.ProductRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductAuditRepository productAuditRepository;
    private final Counter stockConflictCounter;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductMapper productMapper,
            ProductAuditRepository productAuditRepository,
            MeterRegistry meterRegistry) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productAuditRepository = productAuditRepository;

        this.stockConflictCounter = Counter.builder(
                "huellasdulces.stock.conflicts")
                .description("Cantidad de conflictos por stock insuficiente")
                .register(meterRegistry);
    }

    @Override
    @CacheEvict(
            value = {"productCatalog", "productCategories"},
            allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = productMapper.toEntity(productRequest);
        Product savedProduct = productRepository.save(product);

        registerAudit(
                savedProduct,
                "PRODUCT_CREATED",
                "Producto creado",
                null,
                null,
                savedProduct.getStock()
        );

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Cacheable("productCatalog")
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable("productCategories")
    public List<String> getProductCategories() {

        return productRepository.findAll()
                .stream()
                .map(Product::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    @Cacheable(value = "productCatalog", key = "#id")
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto con ID " + id + " no encontrado"
                ));

        return productMapper.toResponse(product);
    }

    @Override
    @CacheEvict(
            value = {"productCatalog", "productCategories"},
            allEntries = true)
    public ProductResponse updateProduct(
            Long id,
            ProductRequest productRequest) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto con ID " + id + " no encontrado"
                ));

        productMapper.updateProductFromRequest(productRequest, product);

        Product updatedProduct = productRepository.save(product);

        registerAudit(
                updatedProduct,
                "PRODUCT_UPDATED",
                "Producto actualizado",
                null,
                null,
                updatedProduct.getStock()
        );

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @CacheEvict(
            value = {"productCatalog", "productCategories"},
            allEntries = true)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto con ID " + id + " no encontrado"
                ));

        registerAudit(
                product,
                "PRODUCT_DELETED",
                "Producto eliminado",
                null,
                product.getStock(),
                null
        );

        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"productCatalog", "productCategories"},
            allEntries = true)
    public ProductResponse withdrawStock(
            Long id,
            StockRequest stockRequest) {

        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Producto con ID " + id + " no encontrado"
                ));

        int currentStock = product.getStock() == null
                ? 0
                : product.getStock();

        int quantity = stockRequest.quantity();

        if (quantity > currentStock) {

            stockConflictCounter.increment();

            throw new StockConflictException(
                    "Stock insuficiente. Existencias: " + currentStock
                    + ", cantidad solicitada: " + quantity
            );
        }

        int newStock = currentStock - quantity;

        product.setStock(newStock);
        product.setAvailable(newStock > 0);

        Product updatedProduct = productRepository.save(product);

        registerAudit(
                updatedProduct,
                "STOCK_WITHDRAWAL",
                "Retiro de " + quantity + " unidades",
                quantity,
                currentStock,
                newStock
        );

        return productMapper.toResponse(updatedProduct);
    }

    private void registerAudit(
            Product product,
            String action,
            String details,
            Integer quantity,
            Integer stockBefore,
            Integer stockAfter) {

        ProductAuditDocument audit = new ProductAuditDocument(
                product.getId(),
                product.getName(),
                action,
                details,
                quantity,
                stockBefore,
                stockAfter
        );

        productAuditRepository.save(audit);
    }
}
