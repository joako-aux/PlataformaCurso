package com.plataformacurso.usuario_service.repository;

import com.plataformacurso.usuario_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Si en el futuro necesitas buscar por email o rol, los defines aquí.
}