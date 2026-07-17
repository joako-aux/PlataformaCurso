package com.plataformacurso.notificacion_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    @Column(nullable = false, length = 500)
    private String mensaje;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo; // Ejemplo: "EMAIL", "PUSH", "SISTEMA"

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @NotBlank(message = "El estado de la notificación es obligatorio")
    private String estado; // Ejemplo: "ENVIADO", "PENDIENTE", "LEIDO"
}