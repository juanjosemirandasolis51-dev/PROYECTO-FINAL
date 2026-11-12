package com.example.subastas.Controller;

import com.example.subastas.Model.Dto.*;
import com.example.subastas.Model.Entity.Usuario;
import com.example.subastas.Service.SubastaService;
import com.example.subastas.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SubastaService subastaService;

    public UsuarioController(UsuarioService us, SubastaService ss) {

        usuarioService = us;
        subastaService = ss;

    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@RequestBody RegistroRequest r) {

        return ResponseEntity.status(201).body(usuarioService.registrar(r));

    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest r) {

        return usuarioService.login(r);

    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String token) {

        usuarioService.logout(token);

    }

    @GetMapping("/usuarios/me/ganadas")
    public List<GanadaResponse> ganadas(@RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return subastaService.ganadas(usuario);

    }

    @GetMapping("/usuarios/me/vendidas")
    public List<VendidaResponse> vendidas(@RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return subastaService.vendidas(usuario);

    }

}
