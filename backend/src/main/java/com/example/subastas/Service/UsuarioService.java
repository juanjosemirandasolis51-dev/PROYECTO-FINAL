package com.example.subastas.Service;

import com.example.subastas.Excepcion.ErrorNegocio;
import com.example.subastas.Model.Dto.LoginRequest;
import com.example.subastas.Model.Dto.LoginResponse;
import com.example.subastas.Model.Dto.RegistroRequest;
import com.example.subastas.Model.Dto.UsuarioResponse;
import com.example.subastas.Model.Entity.Usuario;
import com.example.subastas.Model.Entity.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    private final Map<Long, Usuario> usuarios = new HashMap<>();
    private final Map<String, Long> sesiones = new HashMap<>();

    @Value("${admin.nombre}")
    private String adminNombre;

    @Value("${admin.correo}")
    private String adminCorreo;

    @Value("${admin.contrasena}")
    private String adminContrasena;

    public UsuarioService(UsuarioRepository ur) {

        usuarioRepository = ur;

    }

    @PostConstruct
    public void cargar() {

        if ( usuarioRepository.findByRol("ADMIN").isEmpty() ) {
            Usuario admin = new Usuario(null, adminNombre, adminCorreo, encriptador.encode(adminContrasena), "ADMIN", LocalDateTime.now());
            usuarioRepository.save(admin);
            log.info("Se creo el usuario administrador {}", adminCorreo);
        }

        for ( Usuario u : usuarioRepository.findAll() ) {
            usuarios.put(u.getId(), u);
        }

        log.info("Usuarios cargados en memoria: {}", usuarios.size());

    }

    public synchronized UsuarioResponse registrar(RegistroRequest r) {

        if ( vacio(r.nombre()) || vacio(r.correo()) || vacio(r.contrasena()) ) {
            throw new ErrorNegocio(400, "Llena todos los campos");
        }

        if ( !r.correo().contains("@") ) {
            throw new ErrorNegocio(400, "El correo no es valido");
        }

        if ( r.contrasena().length() < 6 ) {
            throw new ErrorNegocio(400, "La contrasena debe tener al menos 6 caracteres");
        }

        String correo = r.correo().trim().toLowerCase();

        if ( usuarioRepository.findByCorreo(correo) != null ) {
            throw new ErrorNegocio(409, "Ese correo ya esta registrado");
        }

        Usuario usuario = new Usuario(null, r.nombre().trim(), correo, encriptador.encode(r.contrasena()), "USUARIO", LocalDateTime.now());
        usuarioRepository.save(usuario);
        usuarios.put(usuario.getId(), usuario);

        log.info("Usuario registrado {} con id {}", correo, usuario.getId());

        return convertir(usuario);

    }

    public synchronized LoginResponse login(LoginRequest r) {

        if ( vacio(r.correo()) || vacio(r.contrasena()) ) {
            throw new ErrorNegocio(401, "Correo o contrasena incorrectos");
        }

        Usuario usuario = usuarioRepository.findByCorreo(r.correo().trim().toLowerCase());

        if ( usuario == null || !encriptador.matches(r.contrasena(), usuario.getContrasenaHash()) ) {
            log.warn("Intento de login fallido con el correo {}", r.correo());
            throw new ErrorNegocio(401, "Correo o contrasena incorrectos");
        }

        String token = UUID.randomUUID().toString();
        sesiones.put(token, usuario.getId());

        log.info("Inicio de sesion de {}", usuario.getCorreo());

        return new LoginResponse(token, convertir(usuario));

    }

    public synchronized void logout(String encabezado) {

        sesiones.remove(sacarToken(encabezado));

    }

    public synchronized Usuario usuarioDeToken(String encabezado) {

        Long id = sesiones.get(sacarToken(encabezado));

        if ( id == null ) {
            throw new ErrorNegocio(401, "Debes iniciar sesion");
        }

        return usuarios.get(id);

    }

    public synchronized Usuario buscar(Long id) {

        return usuarios.get(id);

    }

    public synchronized String nombreDe(Long id) {

        Usuario usuario = usuarios.get(id);

        if ( usuario == null ) { return "Desconocido"; }

        return usuario.getNombre();

    }

    private String sacarToken(String encabezado) {

        if ( encabezado == null || !encabezado.startsWith("Bearer ") ) { return ""; }

        return encabezado.substring(7);

    }

    private boolean vacio(String texto) {

        return texto == null || texto.trim().isEmpty();

    }

    private UsuarioResponse convertir(Usuario u) {

        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());

    }

}
