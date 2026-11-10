package com.huellasdulces.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "El nombre del producto es obligatorio")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotBlank(message = "La categoría es obligatoria")
        String category,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(
                value = "0.01",
                message = "El precio debe ser mayor a 0"
        )
        BigDecimal price,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(
                message = "El stock no puede ser negativo"
        )
        Integer stock
) {
}
