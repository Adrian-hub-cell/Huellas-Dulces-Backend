package com.huellasdulces.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Huellas Dulces API",
                version = "1.0",
                description = "API para la gestión de productos de Huellas Dulces"
        )
)
public class OpenApiConfig {
}
