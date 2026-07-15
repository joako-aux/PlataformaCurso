package com.plataformacurso.auth_service.Model;

public class UsuarioRequest {
    private String username;
    private String password;

    // Constructor vacío (obligatorio para que Spring/Jackson transformen el JSON que viene del frontend)
    public UsuarioRequest() {
    }

    // Constructor con parámetros
    public UsuarioRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}