package com.plataformacurso.curso_service.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String categoria;
    private String instructor;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "cupo_maximo")
    private Integer cupoMaximo;

    @Column(name = "cupos_disponibles")
    private Integer cuposDisponibles;

    private String modalidad; // Presencial, Online, Híbrido, etc.
    private String estado;     // Activo, Pausado, Finalizado, etc.
}