
package com.huellasdulces.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import com.huellasdulces.config.SecurityConfig;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.repository.CustomerRepository;
import com.huellasdulces.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CustomerRepository customerRepository;

   @MockitoBean
   private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    private ProductResponse createProductResponse() {
        return new ProductResponse(
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
    @WithMockUser(roles = "USER")
    void userShouldConsultProducts() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of(createProductResponse()));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Croquetas para perro"));

        verify(productService).getAllProducts();
    }

    @Test
    void unauthenticatedUserShouldNotConsultProducts()
            throws Exception {

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void userShouldNotCreateProducts() throws Exception {

        String request = """
                {
                    "name": "Croquetas para perro",
                    "description": "Alimento para perros adultos",
                    "category": "Alimentos",
                    "price": 45000.00,
                    "stock": 20
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldCreateProducts() throws Exception {

        String request = """
                {
                    "name": "Croquetas para perro",
                    "description": "Alimento para perros adultos",
                    "category": "Alimentos",
                    "price": 45000.00,
                    "stock": 20
                }
                """;

        when(productService.createProduct(
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(createProductResponse());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Croquetas para perro"));

        verify(productService).createProduct(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void userShouldNotDeleteProducts() throws Exception {

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteProducts() throws Exception {

        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldNotCreateProductWithInvalidData()
            throws Exception {

        String request = """
                {
                    "name": "",
                    "description": "Alimento para perros adultos",
                    "category": "Alimentos",
                    "price": 0,
                    "stock": 20
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }
}
