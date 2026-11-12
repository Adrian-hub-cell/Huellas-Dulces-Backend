
package com.huellasdulces.mapper;

import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapperImpl();
    }

    @Test
    void shouldConvertProductToResponse() {
        Product product = new Product(
                1L,
                "Croquetas para perro",
                "Alimento para perros adultos",
                "Alimentos",
                new BigDecimal("45000.00"),
                true,
                20
        );

        ProductResponse response = mapper.toResponse(product);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Croquetas para perro");
        assertThat(response.description())
                .isEqualTo("Alimento para perros adultos");
        assertThat(response.category()).isEqualTo("Alimentos");
        assertThat(response.price()).isEqualByComparingTo("45000.00");
        assertThat(response.available()).isTrue();
        assertThat(response.stock()).isEqualTo(20);
    }

    @Test
    void shouldReturnNullWhenProductIsNull() {
        ProductResponse response = mapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void shouldConvertRequestToEntity() {
        ProductRequest request = new ProductRequest(
                "Croquetas para gato",
                "Alimento para gatos adultos",
                "Alimentos",
                new BigDecimal("35000.00"),
                15
        );

        Product product = mapper.toEntity(request);

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("Croquetas para gato");
        assertThat(product.getDescription())
                .isEqualTo("Alimento para gatos adultos");
        assertThat(product.getCategory()).isEqualTo("Alimentos");
        assertThat(product.getPrice()).isEqualByComparingTo("35000.00");
        assertThat(product.getAvailable()).isTrue();
        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        Product product = mapper.toEntity(null);

        assertThat(product).isNull();
    }

    @Test
    void shouldUpdateExistingProductWithoutChangingIdOrAvailability() {
        Product product = new Product(
                10L,
                "Producto anterior",
                "Descripción anterior",
                "Accesorios",
                new BigDecimal("10000.00"),
                false,
                5
        );

        ProductRequest request = new ProductRequest(
                "Producto actualizado",
                "Nueva descripción",
                "Alimentos",
                new BigDecimal("25000.00"),
                30
        );

        mapper.updateProductFromRequest(request, product);

        assertThat(product.getId()).isEqualTo(10L);
        assertThat(product.getName()).isEqualTo("Producto actualizado");
        assertThat(product.getDescription()).isEqualTo("Nueva descripción");
        assertThat(product.getCategory()).isEqualTo("Alimentos");
        assertThat(product.getPrice()).isEqualByComparingTo("25000.00");
        assertThat(product.getStock()).isEqualTo(30);
        assertThat(product.getAvailable()).isFalse();
    }

    @Test
    void shouldNotChangeProductWhenUpdateRequestIsNull() {
        Product product = new Product(
                10L,
                "Producto original",
                "Descripción original",
                "Accesorios",
                new BigDecimal("10000.00"),
                true,
                8
        );

        mapper.updateProductFromRequest(null, product);

        assertThat(product.getId()).isEqualTo(10L);
        assertThat(product.getName()).isEqualTo("Producto original");
        assertThat(product.getDescription()).isEqualTo("Descripción original");
        assertThat(product.getCategory()).isEqualTo("Accesorios");
        assertThat(product.getPrice()).isEqualByComparingTo("10000.00");
        assertThat(product.getAvailable()).isTrue();
        assertThat(product.getStock()).isEqualTo(8);
    }
}
