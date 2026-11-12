package com.example.subastas.Excepcion;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorNegocio.class)
    public ResponseEntity<Map<String, String>> errorNegocio(ErrorNegocio e) {

        log.warn("Peticion rechazada {} {}", e.getCodigo(), e.getMessage());

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", e.getMessage());

        return ResponseEntity.status(e.getCodigo()).body(respuesta);

    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> noExiste(NoResourceFoundException e) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "No existe");

        return ResponseEntity.status(404).body(respuesta);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> datosMalos(HttpMessageNotReadableException e) {

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Los datos enviados no son validos");

        return ResponseEntity.status(400).body(respuesta);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> errorGeneral(Exception e) {

        log.error("Error en el sistema: " + e);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Error en el servidor");

        return ResponseEntity.status(500).body(respuesta);

    }

}
