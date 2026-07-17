package com.plataformacurso.historial_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "historiales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull(message = "El ID del curso es obligatorio")
    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @PastOrPresent(message = "La fecha de finalización no puede ser una fecha futura")
    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    @Min(value = 0, message = "La calificación mínima no puede ser menor a 0")
    @Max(value = 10, message = "La calificación máxima no puede ser mayor a 10") // Ajusta el máximo según tu escala (ej: 7 o 100)
    private Double calificacion;

    @Size(max = 255, message = "La referencia del certificado no puede superar los 255 caracteres")
    private String certificado;

    @NotBlank(message = "El estado del historial es obligatorio")
    @Pattern(regexp = "APROBADO|REPROBADO|CURSANDO", message = "El estado debe ser APROBADO, REPROBADO o CURSANDO")
    private String estado;
}