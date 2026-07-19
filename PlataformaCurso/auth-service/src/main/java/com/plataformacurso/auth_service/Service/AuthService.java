package com.plataformacurso.auth_service.Service;

import com.plataformacurso.auth_service.entity.UserAuth;
import com.plataformacurso.auth_service.Repository.UserAuthRepository;
import com.plataformacurso.auth_service.dto.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Registra un usuario en la base de datos MySQL encriptando su contraseña.
     */
    public String registrarUsuario(String email, String rawPassword) {
        // Validamos si el correo ya existe en la base de datos
        if (userAuthRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Encriptamos la contraseña usando BCrypt antes de persistirla
        String passwordEncriptada = passwordEncoder.encode(rawPassword);

        // Creamos la instancia con la entidad correcta UserAuth
        UserAuth nuevoUsuario = new UserAuth(email, passwordEncriptada);
        userAuthRepository.save(nuevoUsuario);

        return "Usuario registrado exitosamente";
    }

    /**
     * Verifica las credenciales del usuario y genera el token JWT.
     */
    public String loginYGenerarToken(String email, String rawPassword) {
        // Buscamos al usuario en la base de datos por su email
        UserAuth usuario = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comparamos la contraseña en texto plano ingresada con el hash guardado en MySQL
        if (passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            // Si la contraseña coincide, generamos el JWT usando el email como 'Subject'
            return jwtService.generateToken(usuario.getEmail());
        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }
}
