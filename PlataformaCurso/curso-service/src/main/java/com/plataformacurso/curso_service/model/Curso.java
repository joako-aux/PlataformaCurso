package com.plataformacurso.curso_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del curso es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "El nombre del instructor es obligatorio")
    private String instructor;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de finalización es obligatoria")
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos de 1 estudiante")
    @Column(name = "cupo_maximo")
    private Integer cupoMaximo;

    @Min(value = 0, message = "Los cupos disponibles no pueden ser negativos")
    @Column(name = "cupos_disponibles")
    private Integer cuposDisponibles;

    @NotBlank(message = "La modalidad es obligatoria")
    @Pattern(regexp = "Presencial|Online|Híbrido", message = "La modalidad debe ser Presencial, Online o Híbrido")
    private String modalidad;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "Activo|Pausado|Finalizado", message = "El estado debe ser Activo, Pausado o Finalizado")
    private String estado;

    @PrePersist
    protected void onCreate() {
        if (this.cuposDisponibles == null) {
            this.cuposDisponibles = this.cupoMaximo; // Al crear, los disponibles son iguales al máximo
        }
        if (this.estado == null) {
            this.estado = "Activo";
        }
    }

    // Validación cruzada para asegurar coherencia lógica en fechas
    @AssertTrue(message = "La fecha de finalización debe ser posterior a la fecha de inicio")
    public boolean isFechaFinValida() {
        if (fechaInicio == null || fechaFin == null) {
            return true; // Deja que @NotNull maneje la ausencia de datos
        }
        return fechaFin.isAfter(fechaInicio);
    }
}