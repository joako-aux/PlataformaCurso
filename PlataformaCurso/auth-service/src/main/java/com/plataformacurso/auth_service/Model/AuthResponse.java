package com.plataformacurso.auth_service.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo de respuesta que contiene el token JWT")
public class AuthResponse {

    @Schema(description = "Token de acceso generado (JWT)", example = "eyJhbGciOiJIUzI1NiIsIn...")
    private String token;
}