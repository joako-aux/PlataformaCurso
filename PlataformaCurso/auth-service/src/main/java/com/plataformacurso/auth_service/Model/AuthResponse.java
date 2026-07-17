package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta que contiene el token JWT generado")
public class AuthResponse {

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token de acceso generado")
    private String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}