package com.example.subastas.Model.Dto;

public record PublicarRequest(

        String nombre,
        String descripcion,
        Double precioBase,
        Integer duracionMinutos

)
{ }
