
package com.huellasdulces.controller;

import com.huellasdulces.service.ExternalCatalogService;
import com.huellasdulces.service.ExternalCatalogService.CatalogValidationResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external-catalog")
public class ExternalCatalogController {

    private final ExternalCatalogService externalCatalogService;

    public ExternalCatalogController(
            ExternalCatalogService externalCatalogService) {
        this.externalCatalogService = externalCatalogService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CatalogValidationResponse> searchProducts(
            @RequestParam String name) {

        CatalogValidationResponse response =
                externalCatalogService.searchProducts(name);

        return ResponseEntity.ok(response);
    }
}
