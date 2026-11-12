package com.example.subastas.Model.Dto;

public record VendidaResponse(

        Long id,
        String nombre,
        String estado,
        double precioBase,
        Double precioFinal,
        String ganadorNombre

)
{ }
