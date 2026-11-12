package com.example.subastas.Model.Dto;

import java.time.LocalDateTime;

public record PujaResponse(

        Long id,
        String usuarioNombre,
        double monto,
        LocalDateTime fechaHora

)
{ }
