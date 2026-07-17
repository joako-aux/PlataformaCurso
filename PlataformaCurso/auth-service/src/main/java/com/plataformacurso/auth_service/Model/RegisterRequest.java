package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Modelo para el registro (Solo email y password)")
public class RegisterRequest {

    @Schema(description = "Correo electrónico único", example = "moises@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Contraseña segura", example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}