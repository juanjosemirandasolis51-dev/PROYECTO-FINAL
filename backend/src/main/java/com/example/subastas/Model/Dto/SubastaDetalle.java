package com.example.subastas.Model.Dto;

import java.time.LocalDateTime;

public record SubastaDetalle(

        Long id,
        String nombre,
        String descripcion,
        double precioBase,
        Double pujaMasAlta,
        PersonaResponse ganadorActual,
        PersonaResponse vendedor,
        String estado,
        LocalDateTime fechaFin,
        long segundosRestantes

)
{ }
