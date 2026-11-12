
package com.huellasdulces.controller;

import com.huellasdulces.config.SecurityConfig;
import com.huellasdulces.repository.CustomerRepository;
import com.huellasdulces.service.ExternalCatalogService;
import com.huellasdulces.service.ExternalCatalogService.CatalogValidationResponse;
import com.huellasdulces.service.ExternalCatalogService.ExternalProduct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalCatalogController.class)
@Import(SecurityConfig.class)
class ExternalCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalCatalogService externalCatalogService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "USER")
    void userDebeConsultarCatalogoExterno() throws Exception {

        when(externalCatalogService.searchProducts("croquetas"))
                .thenReturn(new CatalogValidationResponse(
                        false,
                        "Consulta externa realizada correctamente.",
                        List.of(new ExternalProduct(
                                "Croquetas para perro",
                                "Marca Animal",
                                "123456789"
                        ))
                ));

        mockMvc.perform(get("/api/v1/external-catalog/search")
                        .param("name", "croquetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manualValidationRequired")
                        .value(false))
                .andExpect(jsonPath("$.message")
                        .value("Consulta externa realizada correctamente."))
                .andExpect(jsonPath("$.products[0].name")
                        .value("Croquetas para perro"))
                .andExpect(jsonPath("$.products[0].brand")
                        .value("Marca Animal"))
                .andExpect(jsonPath("$.products[0].barcode")
                        .value("123456789"));

        verify(externalCatalogService).searchProducts("croquetas");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDebeConsultarCatalogoExterno() throws Exception {

        when(externalCatalogService.searchProducts("alimento"))
                .thenReturn(new CatalogValidationResponse(
                        false,
                        "Consulta externa realizada correctamente.",
                        List.of()
                ));

        mockMvc.perform(get("/api/v1/external-catalog/search")
                        .param("name", "alimento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isEmpty());

        verify(externalCatalogService).searchProducts("alimento");
    }

    @Test
    void usuarioNoAutenticadoNoDebeConsultarCatalogo()
            throws Exception {

        mockMvc.perform(get("/api/v1/external-catalog/search")
                        .param("name", "croquetas"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(externalCatalogService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void debeRechazarConsultaSinNombre() throws Exception {

        mockMvc.perform(get("/api/v1/external-catalog/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(externalCatalogService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void debeMostrarRespuestaDeValidacionManual()
            throws Exception {

        when(externalCatalogService.searchProducts("producto desconocido"))
                .thenReturn(new CatalogValidationResponse(
                        true,
                        "Validacion Manual Requerida por Huellas Dulces",
                        List.of()
                ));

        mockMvc.perform(get("/api/v1/external-catalog/search")
                        .param("name", "producto desconocido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manualValidationRequired")
                        .value(true))
                .andExpect(jsonPath("$.message")
                        .value("Validacion Manual Requerida por Huellas Dulces"))
                .andExpect(jsonPath("$.products").isEmpty());

        verify(externalCatalogService)
                .searchProducts("producto desconocido");
    }
}
