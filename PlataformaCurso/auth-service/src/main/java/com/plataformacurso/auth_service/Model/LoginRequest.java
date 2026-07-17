package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para la solicitud de inicio de sesión")
public class LoginRequest {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Schema(example = "joaquin.perez", description = "Nombre de usuario o correo electrónico")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Schema(example = "Password123!", description = "Contraseña de la cuenta")
    private String password;
}