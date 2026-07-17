package com.plataformacurso.notificacion_service.model;

import jakarta.persistence.*;
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

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 500)
    private String mensaje;

    private String tipo; // Ejemplo: "EMAIL", "PUSH", "SISTEMA"

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    private String estado; // Ejemplo: "ENVIADO", "PENDIENTE", "LEIDO"
}