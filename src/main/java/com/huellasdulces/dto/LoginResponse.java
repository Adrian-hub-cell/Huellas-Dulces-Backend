
package com.huellasdulces.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
