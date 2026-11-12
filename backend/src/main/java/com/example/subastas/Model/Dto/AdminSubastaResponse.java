package com.example.subastas.Model.Dto;

public record AdminSubastaResponse(

        Long id,
        String nombre,
        String vendedorNombre,
        String estado,
        double precioActual,
        long segundosRestantes

)
{ }
