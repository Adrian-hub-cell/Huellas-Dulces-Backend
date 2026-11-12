package com.huellasdulces.service;

import com.huellasdulces.dto.StockRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTddTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductAuditRepository productAuditRepository;

    private ProductServiceImpl productService;
    private Product product;

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
    }

    @Test
    void shouldRejectNegativeStockWithdrawal() {
        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                productService.withdrawStock(1L, new StockRequest(-5))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La cantidad debe ser mayor que cero");

        verify(productRepository, never()).save(any());
        verify(productAuditRepository, never()).save(any());
    }
}
