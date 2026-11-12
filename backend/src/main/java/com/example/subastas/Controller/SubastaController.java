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
public class SubastaController {

    private final UsuarioService usuarioService;
    private final SubastaService subastaService;

    public SubastaController(UsuarioService us, SubastaService ss) {

        usuarioService = us;
        subastaService = ss;

    }

    @GetMapping("/subastas")
    public List<SubastaResumen> listar(@RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam(required = false) String nombre) {

        usuarioService.usuarioDeToken(token);

        return subastaService.listarActivas(nombre);

    }

    @PostMapping("/subastas")
    public ResponseEntity<SubastaDetalle> publicar(@RequestHeader(value = "Authorization", required = false) String token,
                                                   @RequestBody PublicarRequest r) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return ResponseEntity.status(201).body(subastaService.publicar(usuario, r));

    }

    @GetMapping("/subastas/{id}")
    public SubastaDetalle detalle(@RequestHeader(value = "Authorization", required = false) String token,
                                  @PathVariable Long id) {

        usuarioService.usuarioDeToken(token);

        return subastaService.verDetalle(id);

    }

    @GetMapping("/subastas/{id}/pujas")
    public List<PujaResponse> pujas(@RequestHeader(value = "Authorization", required = false) String token,
                                    @PathVariable Long id) {

        usuarioService.usuarioDeToken(token);

        return subastaService.verPujas(id);

    }

    @PostMapping("/subastas/{id}/pujas")
    public ResponseEntity<PujaResponse> pujar(@RequestHeader(value = "Authorization", required = false) String token,
                                              @PathVariable Long id,
                                              @RequestBody PujaRequest r) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return ResponseEntity.status(201).body(subastaService.pujar(usuario, id, r));

    }

    @GetMapping("/admin/subastas")
    public List<AdminSubastaResponse> todas(@RequestHeader(value = "Authorization", required = false) String token) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return subastaService.todas(usuario);

    }

    @PostMapping("/admin/subastas/{id}/cancelar")
    public SubastaDetalle cancelar(@RequestHeader(value = "Authorization", required = false) String token,
                                   @PathVariable Long id) {

        Usuario usuario = usuarioService.usuarioDeToken(token);

        return subastaService.cancelar(usuario, id);

    }

}
