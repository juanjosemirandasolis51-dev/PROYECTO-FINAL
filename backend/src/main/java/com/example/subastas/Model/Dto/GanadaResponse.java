package com.example.subastas.Model.Dto;

import java.time.LocalDateTime;

public record GanadaResponse(

        Long productoId,
        String nombre,
        double precioFinal,
        LocalDateTime fechaCierre,
        String vendedorNombre

)
{ }
