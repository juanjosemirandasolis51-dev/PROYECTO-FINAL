package com.example.subastas.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ganadores")
public class Ganador {

    @Id
    private Long productoId;
    private Long usuarioId;
    private double precioFinal;
    private LocalDateTime fechaCierre;

}
