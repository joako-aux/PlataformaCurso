package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Modelo para la solicitud de inicio de sesión")
public class LoginRequest {

    @Schema(description = "Correo electrónico del usuario", example = "moises@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña en texto plano", example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}