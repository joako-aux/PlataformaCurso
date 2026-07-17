package com.plataformacurso.inscripciones_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull(message = "El ID del curso es obligatorio")
    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(name = "fecha_inscripcion")
    private LocalDate fechaInscripcion;

    @NotBlank(message = "El estado de la inscripción es obligatorio")
    @Pattern(regexp = "ACTIVA|INACTIVA|CANCELADA", message = "El estado debe ser ACTIVA, INACTIVA o CANCELADA")
    private String estado;

    @PrePersist
    protected void onCreate() {
        this.fechaInscripcion = LocalDate.now();
        if (this.estado == null) {
            this.estado = "ACTIVA"; // Estado por defecto si no mandan ninguno
        }
    }
}