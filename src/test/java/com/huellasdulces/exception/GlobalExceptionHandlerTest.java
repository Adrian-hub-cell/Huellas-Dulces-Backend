
package com.huellasdulces.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleProductNotFound() {
        ProductNotFoundException exception =
                new ProductNotFoundException("Producto no encontrado");

        ProblemDetail result = handler.handleProductNotFound(exception);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getDetail()).isEqualTo("Producto no encontrado");
        assertThat(result.getTitle()).isEqualTo("Producto no encontrado");
        assertThat(result.getType()).isEqualTo(
                URI.create("https://huellasdulces.com/errors/product-not-found")
        );
    }

    @Test
    void shouldHandleStockConflict() {
        StockConflictException exception =
                new StockConflictException("Stock insuficiente");

        ProblemDetail result = handler.handleStockConflict(exception);

        assertThat(result.getStatus()).isEqualTo(409);
        assertThat(result.getDetail()).isEqualTo("Stock insuficiente");
        assertThat(result.getTitle()).isEqualTo("Conflicto de inventario");
        assertThat(result.getType()).isEqualTo(
                URI.create("https://huellasdulces.com/errors/stock-conflict")
        );
    }

    @Test
    void shouldHandleValidationErrorWithFieldMessage() {
        BindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");

        bindingResult.addError(
                new FieldError("request", "name", "El nombre es obligatorio")
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail result = handler.handleValidationException(exception);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("El nombre es obligatorio");
        assertThat(result.getTitle()).isEqualTo("Error de validacion");
        assertThat(result.getType()).isEqualTo(
                URI.create("https://huellasdulces.com/errors/validation-error")
        );
    }

    @Test
    void shouldUseDefaultMessageWhenThereAreNoFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail result = handler.handleValidationException(exception);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("Error de validacion");
    }
}
