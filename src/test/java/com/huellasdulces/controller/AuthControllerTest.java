
package com.huellasdulces.controller;

import com.huellasdulces.config.SecurityConfig;
import com.huellasdulces.dto.LoginRequest;
import com.huellasdulces.dto.LoginResponse;
import com.huellasdulces.service.AuthService;
import com.huellasdulces.service.CustomerRegistrationService;
import com.huellasdulces.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomerRegistrationService registrationService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void loginDebeRetornarTokenCuandoLosDatosSonValidos()
            throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse(
                        "token-prueba",
                        "Bearer"
                ));

        String request = """
                {
                    "username": "admin",
                    "password": "Admin123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("token-prueba"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"));
    }

    @Test
    void loginDebeRechazarCamposVacios() throws Exception {

        String request = """
                {
                    "username": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDebeRetornarCreatedCuandoLosDatosSonValidos()
            throws Exception {

        when(registrationService.register(any()))
                .thenReturn(new com.huellasdulces.dto.CustomerRegisterResponse(
                        1L,
                        "cliente1",
                        "cliente@correo.com",
                        "Registro exitoso"
                ));

        String request = """
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "identificationNumber": "123456789",
                    "email": "cliente@correo.com",
                    "phone": "3001234567",
                    "address": "Calle 10",
                    "username": "cliente1",
                    "password": "Cliente123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("cliente1"))
                .andExpect(jsonPath("$.email")
                        .value("cliente@correo.com"))
                .andExpect(jsonPath("$.message")
                        .value("Registro exitoso"));
    }

    @Test
    void registerDebeRechazarCamposObligatoriosVacios()
            throws Exception {

        String request = """
                {
                    "firstName": "",
                    "lastName": "",
                    "identificationNumber": "",
                    "email": "",
                    "phone": "",
                    "username": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDebeRechazarCorreoInvalido()
            throws Exception {

        String request = """
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "identificationNumber": "123456789",
                    "email": "correo-invalido",
                    "phone": "3001234567",
                    "username": "cliente1",
                    "password": "Cliente123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDebeRechazarUsuarioDemasiadoCorto()
            throws Exception {

        String request = """
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "identificationNumber": "123456789",
                    "email": "cliente@correo.com",
                    "phone": "3001234567",
                    "username": "abc",
                    "password": "Cliente123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDebeRechazarContrasenaDemasiadoCorta()
            throws Exception {

        String request = """
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "identificationNumber": "123456789",
                    "email": "cliente@correo.com",
                    "phone": "3001234567",
                    "username": "cliente1",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}

