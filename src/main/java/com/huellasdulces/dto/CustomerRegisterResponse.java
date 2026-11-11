package com.huellasdulces.dto;

public record CustomerRegisterResponse(
        Long id,
        String username,
        String email,
        String message
) {
}