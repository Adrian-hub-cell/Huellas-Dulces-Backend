package com.huellasdulces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRegisterRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "La identificacion es obligatoria")
        @Size(max = 30)
        String identificationNumber,

        @NotBlank(message = "El correo electronico es obligatorio")
        @Email(message = "El correo electronico no es valido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 30)
        String phone,

        @Size(max = 255)
        String address,

        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 80)
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72)
        String password
) {
}