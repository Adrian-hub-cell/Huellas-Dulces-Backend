
package com.huellasdulces.controller;

import com.huellasdulces.config.SecurityConfig;
import com.huellasdulces.dto.UserProfileAvatar;
import com.huellasdulces.dto.UserProfileResponse;
import com.huellasdulces.service.UserProfileService;
import com.huellasdulces.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@Import(SecurityConfig.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService service;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private UserProfileResponse profile() {
        return new UserProfileResponse(
                1L,
                "adrian",
                "Adrian",
                "adrian@example.com",
                "3001234567",
                "Medellin",
                true,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 2, 10, 0)
        );
    }

    @Test
    void shouldGetMyProfile() throws Exception {
        when(service.getProfile("adrian")).thenReturn(profile());

        mockMvc.perform(get("/api/v1/profile/me").with(user("adrian")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adrian"))
                .andExpect(jsonPath("$.displayName").value("Adrian"))
                .andExpect(jsonPath("$.hasAvatar").value(true));

        verify(service).getProfile("adrian");
    }

    @Test
    void shouldUpdateMyProfile() throws Exception {
        when(service.updateProfile(eq("adrian"), any())).thenReturn(profile());

        mockMvc.perform(put("/api/v1/profile/me")
                        .with(user("adrian"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Adrian",
                                  "email": "adrian@example.com",
                                  "phone": "3001234567",
                                  "address": "Medellin"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adrian"));

        verify(service).updateProfile(eq("adrian"), any());
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(put("/api/v1/profile/me")
                        .with(user("adrian"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayName": "Adrian",
                                  "email": "correo-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(service, never()).updateProfile(any(), any());
    }

    @Test
    void shouldUploadMyAvatar() throws Exception {
        byte[] webp = new byte[] {
                'R', 'I', 'F', 'F', 0, 0, 0, 0,
                'W', 'E', 'B', 'P'
        };

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.webp",
                "image/webp",
                webp
        );

        when(service.uploadAvatar("adrian", file)).thenReturn(profile());

        mockMvc.perform(multipart("/api/v1/profile/me/avatar")
                        .file(file)
                        .with(user("adrian"))
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("adrian"));

        verify(service).uploadAvatar("adrian", file);
    }

    @Test
    void shouldGetMyAvatar() throws Exception {
        byte[] data = new byte[] {1, 2, 3, 4};

        when(service.getAvatar("adrian"))
                .thenReturn(new UserProfileAvatar(data, "image/webp"));

        mockMvc.perform(get("/api/v1/profile/me/avatar").with(user("adrian")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/webp"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("Cache-Control"))
                .andExpect(content().bytes(data));

        verify(service).getAvatar("adrian");
    }

    @Test
    void shouldDeleteMyAvatar() throws Exception {
        mockMvc.perform(delete("/api/v1/profile/me/avatar")
                        .with(user("adrian"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deleteAvatar("adrian");
    }

    @Test
    void shouldRejectAvatarUploadWithoutFile() throws Exception {
        mockMvc.perform(multipart("/api/v1/profile/me/avatar")
                        .with(user("adrian"))
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest());

        verify(service, never()).uploadAvatar(any(), any());
    }

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/profile/me"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }
}
