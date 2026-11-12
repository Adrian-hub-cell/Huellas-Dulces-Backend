
package com.huellasdulces.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExternalCatalogServiceTest {

    private SimpleMeterRegistry meterRegistry;
    private ExternalCatalogService service;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://world.openpetfoodfacts.org")
                .defaultHeader("User-Agent", "HuellasDulces/1.0");

        server = MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder.build();

        service = new ExternalCatalogService(meterRegistry, restClient);
    }

    @Test
    void shouldReturnProductsWhenExternalCatalogFindsMatches() {
        server.expect(requestTo(
                org.hamcrest.Matchers.containsString("/cgi/search.pl")))
                .andExpect(header("User-Agent", "HuellasDulces/1.0"))
                .andRespond(withSuccess("""
                        {
                          "products": [
                            {
                              "product_name": "Croquetas para perro",
                              "brands": "Huellas",
                              "code": "123456"
                            },
                            {
                              "product_name": "Alimento para cachorro",
                              "brands": "PetFood",
                              "code": "789012"
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProducts("croquetas");

        assertThat(response.manualValidationRequired()).isFalse();
        assertThat(response.message())
                .isEqualTo("Consulta externa realizada correctamente.");
        assertThat(response.products()).hasSize(2);
        assertThat(response.products().get(0).name())
                .isEqualTo("Croquetas para perro");
        assertThat(response.products().get(0).brand())
                .isEqualTo("Huellas");
        assertThat(response.products().get(0).barcode())
                .isEqualTo("123456");

        server.verify();
    }

    @Test
    void shouldReturnEmptyMessageWhenNoProductsAreFound() {
        server.expect(requestTo(
                org.hamcrest.Matchers.containsString("/cgi/search.pl")))
                .andRespond(withSuccess("""
                        {
                          "products": []
                        }
                        """, MediaType.APPLICATION_JSON));

        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProducts("producto inexistente");

        assertThat(response.manualValidationRequired()).isFalse();
        assertThat(response.message())
                .isEqualTo("No se encontraron coincidencias en el catalogo externo.");
        assertThat(response.products()).isEmpty();

        server.verify();
    }

    @Test
    void shouldUseDefaultValuesWhenProductFieldsAreMissing() {
        server.expect(requestTo(
                org.hamcrest.Matchers.containsString("/cgi/search.pl")))
                .andRespond(withSuccess("""
                        {
                          "products": [
                            {}
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProducts("croquetas");

        assertThat(response.products()).hasSize(1);
        assertThat(response.products().get(0).name()).isEqualTo("Sin nombre");
        assertThat(response.products().get(0).brand()).isEqualTo("Sin marca");
        assertThat(response.products().get(0).barcode()).isEmpty();

        server.verify();
    }

    @Test
    void shouldReturnEmptyProductsWhenResponseHasNoProductsArray() {
        server.expect(requestTo(
                org.hamcrest.Matchers.containsString("/cgi/search.pl")))
                .andRespond(withSuccess("""
                        {
                          "status": 0
                        }
                        """, MediaType.APPLICATION_JSON));

        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProducts("croquetas");

        assertThat(response.manualValidationRequired()).isFalse();
        assertThat(response.products()).isEmpty();

        server.verify();
    }

    @Test
    void shouldActivateFallbackAndIncrementCounter() {
        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProductsFallback(
                        "croquetas",
                        new RuntimeException("Servicio externo no disponible"));

        assertThat(response.manualValidationRequired()).isTrue();
        assertThat(response.message())
                .isEqualTo("Validacion Manual Requerida por Huellas Dulces");
        assertThat(response.products()).isEmpty();

        assertThat(meterRegistry
                .counter("huellasdulces.catalog.fallbacks")
                .count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementFallbackCounterOnEveryActivation() {
        service.searchProductsFallback("croquetas", new RuntimeException("Error 1"));
        service.searchProductsFallback("alimento", new RuntimeException("Error 2"));

        assertThat(meterRegistry
                .counter("huellasdulces.catalog.fallbacks")
                .count()).isEqualTo(2.0);
    }

    @Test
    void shouldReturnEmptyProductsWhenExternalResponseBodyIsNull() {
        server.expect(requestTo(
                org.hamcrest.Matchers.containsString("/cgi/search.pl")))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        ExternalCatalogService.CatalogValidationResponse response =
                service.searchProducts("croquetas");

        assertThat(response.manualValidationRequired()).isFalse();
        assertThat(response.message())
                .isEqualTo("No se encontraron coincidencias en el catalogo externo.");
        assertThat(response.products()).isEmpty();

        server.verify();
    }
}

