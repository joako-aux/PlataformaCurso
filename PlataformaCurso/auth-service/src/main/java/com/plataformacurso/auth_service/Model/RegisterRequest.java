package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para el registro de nuevos usuarios")
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(example = "moise.fonseca")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(example = "SecurePass456!")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(example = "ROLE_USER", description = "Roles permitidos: ROLE_USER, ROLE_ADMIN")
    private String role;
}