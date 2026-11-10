package com.huellasdulces.dto;

import java.math.BigDecimal;

public record ProductResponse(

        Long id,

        String name,

        String description,

        String category,

        BigDecimal price,

        Boolean available,

        Integer stock
) {
}
