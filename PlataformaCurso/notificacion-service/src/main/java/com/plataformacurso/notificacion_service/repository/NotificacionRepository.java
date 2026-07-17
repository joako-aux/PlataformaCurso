package com.plataformacurso.notificacion_service.repository;

import com.plataformacurso.notificacion_service.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Para recuperar la bandeja de notificaciones de un usuario específico
    List<Notificacion> findByUsuarioId(Long usuarioId);
}