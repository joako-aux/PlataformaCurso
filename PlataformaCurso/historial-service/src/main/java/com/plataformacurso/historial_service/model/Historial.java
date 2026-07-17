package com.plataformacurso.historial_service.model;

import jakarta.persistence.*;
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

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    private Double calificacion;

    private String certificado; // Puede ser un String con la URL del certificado o un código

    private String estado; // Ejemplo: "APROBADO", "REPROBADO", "CURSANDO"
}