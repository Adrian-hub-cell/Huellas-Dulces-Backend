package com.huellasdulces.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String displayName,
        String email,
        String phone,
        String address,
        boolean hasAvatar,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}