package com.example.subastas.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long vendedorId;
    private String nombre;
    private String descripcion;
    private double precioBase;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;

}
