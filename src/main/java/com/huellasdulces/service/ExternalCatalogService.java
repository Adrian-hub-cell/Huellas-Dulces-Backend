

package com.huellasdulces.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import tools.jackson.databind.JsonNode;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalCatalogService {

    private static final Logger log =
            LoggerFactory.getLogger(ExternalCatalogService.class);

    private final RestClient restClient;
    private final Counter catalogFallbackCounter;

    @Autowired
    public ExternalCatalogService(MeterRegistry meterRegistry) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofMillis(1500));
        requestFactory.setReadTimeout(Duration.ofMillis(1500));

        this.restClient = RestClient.builder()
                .baseUrl("https://world.openpetfoodfacts.org")
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "HuellasDulces/1.0")
                .build();

        this.catalogFallbackCounter = createFallbackCounter(meterRegistry);
    }

    ExternalCatalogService(
            MeterRegistry meterRegistry,
            RestClient restClient) {

        this.restClient = restClient;
        this.catalogFallbackCounter = createFallbackCounter(meterRegistry);
    }

    private Counter createFallbackCounter(MeterRegistry meterRegistry) {
        return Counter.builder("huellasdulces.catalog.fallbacks")
                .description("Cantidad de activaciones del respaldo del catalogo externo")
                .register(meterRegistry);
    }

    @CircuitBreaker(
            name = "petFoodCatalog",
            fallbackMethod = "searchProductsFallback"
    )
    public CatalogValidationResponse searchProducts(String productName) {

        log.info("Iniciando consulta al catalogo externo.");

        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cgi/search.pl")
                        .queryParam("search_terms", productName)
                        .queryParam("search_simple", "1")
                        .queryParam("action", "process")
                        .queryParam("json", "1")
                        .queryParam("page_size", "5")
                        .queryParam("fields", "product_name,brands,code")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        List<ExternalProduct> products = new ArrayList<>();

        if (response != null && response.path("products").isArray()) {

            for (JsonNode product : response.path("products")) {

                products.add(new ExternalProduct(
                        product.path("product_name").asText("Sin nombre"),
                        product.path("brands").asText("Sin marca"),
                        product.path("code").asText("")
                ));
            }
        }

        log.info(
                "El catalogo externo respondio con {} productos",
                products.size()
        );

        return new CatalogValidationResponse(
                false,
                products.isEmpty()
                        ? "No se encontraron coincidencias en el catalogo externo."
                        : "Consulta externa realizada correctamente.",
                products
        );
    }

    public CatalogValidationResponse searchProductsFallback(
            String productName,
            Throwable exception) {

        catalogFallbackCounter.increment();

        log.error("Fallo la consulta del catalogo externo.");

        return new CatalogValidationResponse(
                true,
                "Validacion Manual Requerida por Huellas Dulces",
                List.of()
        );
    }

    public record CatalogValidationResponse(
            boolean manualValidationRequired,
            String message,
            List<ExternalProduct> products) {
    }

    public record ExternalProduct(
            String name,
            String brand,
            String barcode) {
    }
}
