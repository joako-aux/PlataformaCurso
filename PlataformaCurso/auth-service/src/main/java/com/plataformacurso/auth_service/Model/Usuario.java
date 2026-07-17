package com.plataformacurso.auth_service.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // ==========================================
    //   MÉTODOS OBLIGATORIOS DE USERDETAILS
    // ==========================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora devolvemos una lista vacía de permisos/roles.
        // Si más adelante manejas roles (Admin, User), aquí mapearás tu campo rol.
        return List.of();
    }

    @Override
    public String getUsername() {
        // Le indicamos a Spring Security que el campo que usamos como "usuario" es el email
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales/contraseña no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // El usuario está activo
    }
}