
package com.huellasdulces.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithEmptyConstructor() {
        Product product = new Product();

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getCategory());
        assertNull(product.getPrice());
        assertNull(product.getAvailable());
        assertNull(product.getStock());
        assertNull(product.getCreatedBy());
        assertNull(product.getCreatedAt());
        assertNull(product.getLastModifiedBy());
        assertNull(product.getLastModifiedAt());
    }

    @Test
    void shouldCreateProductWithAllArguments() {
        Product product = new Product(
                1L,
                "Croquetas para perro",
                "Alimento para perros adultos",
                "Alimentos",
                new BigDecimal("85000.00"),
                true,
                25
        );

        assertEquals(1L, product.getId());
        assertEquals("Croquetas para perro", product.getName());
        assertEquals("Alimento para perros adultos", product.getDescription());
        assertEquals("Alimentos", product.getCategory());
        assertEquals(new BigDecimal("85000.00"), product.getPrice());
        assertTrue(product.getAvailable());
        assertEquals(25, product.getStock());
    }

    @Test
    void shouldUpdateAndRetrieveAllProductFields() {
        Product product = new Product();

        product.setId(2L);
        product.setName("Juguete para gato");
        product.setDescription("Juguete interactivo");
        product.setCategory("Juguetes");
        product.setPrice(new BigDecimal("25000.00"));
        product.setAvailable(false);
        product.setStock(10);

        assertEquals(2L, product.getId());
        assertEquals("Juguete para gato", product.getName());
        assertEquals("Juguete interactivo", product.getDescription());
        assertEquals("Juguetes", product.getCategory());
        assertEquals(new BigDecimal("25000.00"), product.getPrice());
        assertFalse(product.getAvailable());
        assertEquals(10, product.getStock());
    }

    @Test
    void shouldAllowSettingNullValues() {
        Product product = new Product();

        product.setId(null);
        product.setName(null);
        product.setDescription(null);
        product.setCategory(null);
        product.setPrice(null);
        product.setAvailable(null);
        product.setStock(null);

        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getCategory());
        assertNull(product.getPrice());
        assertNull(product.getAvailable());
        assertNull(product.getStock());
    }
}
