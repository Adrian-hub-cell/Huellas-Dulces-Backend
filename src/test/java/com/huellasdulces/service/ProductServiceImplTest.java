package com.huellasdulces.service;

import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.dto.StockRequest;
import com.huellasdulces.exception.ProductNotFoundException;
import com.huellasdulces.exception.StockConflictException;
import com.huellasdulces.mapper.ProductMapper;
import com.huellasdulces.model.Product;
import com.huellasdulces.repository.ProductAuditRepository;
import com.huellasdulces.repository.ProductRepository;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductAuditRepository productAuditRepository;

    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                productMapper,
                productAuditRepository,
                new SimpleMeterRegistry()
        );

        product = new Product(
                1L,
                "Croquetas para perro",
                "Alimento para perros adultos",
                "Alimentos",
                new BigDecimal("45000.00"),
                true,
                20
        );

        productRequest = new ProductRequest(
                "Croquetas para perro",
                "Alimento para perros adultos",
                "Alimentos",
                new BigDecimal("45000.00"),
                20
        );

        productResponse = new ProductResponse(
                1L,
                "Croquetas para perro",
                "Alimento para perros adultos",
                "Alimentos",
                new BigDecimal("45000.00"),
                true,
                20
        );
    }

    @Test
    void shouldCreateProduct() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.createProduct(productRequest);

        assertThat(result).isEqualTo(productResponse);

        verify(productMapper).toEntity(productRequest);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(product);
    }

    @Test
    void shouldGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result)
                .hasSize(1)
                .containsExactly(productResponse);

        verify(productRepository).findAll();
        verify(productMapper).toResponse(product);
    }

    @Test
    void shouldSearchProductsByTerm() {
        when(productRepository.searchByTerm("croquetas"))
                .thenReturn(List.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(productResponse);

        List<ProductResponse> result =
                productService.searchProducts("croquetas");

        assertThat(result)
                .hasSize(1)
                .containsExactly(productResponse);

        verify(productRepository).searchByTerm("croquetas");
    }

    @Test
    void shouldReturnAllProductsWhenSearchTermIsBlank() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> result = productService.searchProducts("   ");

        assertThat(result)
                .hasSize(1)
                .containsExactly(productResponse);

        verify(productRepository).findAll();
        verify(productRepository, never()).searchByTerm(any());
    }

    @Test
    void shouldGetProductCategories() {
        Product secondProduct = new Product(
                2L,
                "Juguete para perro",
                "Juguete resistente",
                "Juguetes",
                new BigDecimal("15000.00"),
                true,
                10
        );

        Product thirdProduct = new Product(
                3L,
                "Otro alimento",
                "Alimento para gatos",
                "Alimentos",
                new BigDecimal("30000.00"),
                true,
                5
        );

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(product, secondProduct, thirdProduct));

        List<String> result = productService.getProductCategories();

        assertThat(result)
                .containsExactly("Alimentos", "Juguetes");
    }

    @Test
    void shouldGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getProductById(1L);

        assertThat(result).isEqualTo(productResponse);

        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result =
                productService.updateProduct(1L, productRequest);

        assertThat(result).isEqualTo(productResponse);

        verify(productRepository).findById(1L);
        verify(productMapper).updateProductFromRequest(productRequest, product);
        verify(productRepository).save(product);
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).deleteById(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void shouldWithdrawStockSuccessfully() {
        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result =
                productService.withdrawStock(1L, new StockRequest(5));

        assertThat(product.getStock()).isEqualTo(15);
        assertThat(product.getAvailable()).isTrue();
        assertThat(result).isEqualTo(productResponse);

        verify(productRepository).findByIdForUpdate(1L);
        verify(productRepository).save(product);
        verify(productAuditRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreStockThanAvailable() {
        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(
                () -> productService.withdrawStock(1L, new StockRequest(50))
        ).isInstanceOf(StockConflictException.class);

        assertThat(product.getStock()).isEqualTo(20);

        verify(productRepository, never()).save(any(Product.class));
        verify(productAuditRepository, never()).save(any());
    }
}

