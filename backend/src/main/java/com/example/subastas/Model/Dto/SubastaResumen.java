package com.example.subastas.Model.Dto;

public record SubastaResumen(

        Long id,
        String nombre,
        double precioActual,
        long segundosRestantes,
        String vendedorNombre,
        int cantidadPujas

)
{ }
