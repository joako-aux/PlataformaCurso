package com.plataformacurso.auth_service.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "userauth")
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Constructor vacío requerido por JPA
    public UserAuth() {}

    // Constructor con parámetros corregido
    public UserAuth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}