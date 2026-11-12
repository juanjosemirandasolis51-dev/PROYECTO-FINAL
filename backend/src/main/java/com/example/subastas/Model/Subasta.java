package com.example.subastas.Model;

import com.example.subastas.Estructura.ColaPujas;
import com.example.subastas.Excepcion.ErrorNegocio;
import com.example.subastas.Model.Entity.Producto;
import com.example.subastas.Model.Entity.Puja;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subasta implements Comparable<Subasta> {

    private Producto producto;
    private ColaPujas ofertas;
    private List<Puja> historial;

    public Subasta(Producto producto) {

        this.producto = producto;
        this.ofertas = new ColaPujas();
        this.historial = new ArrayList<>();

    }

    public Producto getProducto() {
        return producto;
    }

    public List<Puja> getHistorial() {
        return historial;
    }

    public void agregarPuja(Puja puja) {

        ofertas.agregar(puja);
        historial.add(puja);

    }

    public Puja getMejorPuja() {

        return ofertas.verMayor();

    }

    public double getPrecioActual() {

        if ( ofertas.estaVacia() ) { return producto.getPrecioBase(); }

        return ofertas.verMayor().getMonto();

    }

    public long getSegundosRestantes() {

        long segundos = Duration.between(LocalDateTime.now(), producto.getFechaFin()).getSeconds();

        if ( segundos < 0 ) { return 0; }

        return segundos;

    }

    public boolean estaActiva() {

        return producto.getEstado().equals("ACTIVA");

    }

    public void validarPuja(Long usuarioId, Double monto) {

        if ( !estaActiva() ) {
            throw new ErrorNegocio(400, "La subasta ya no esta activa");
        }

        if ( producto.getVendedorId().equals(usuarioId) ) {
            throw new ErrorNegocio(403, "No puedes pujar en tu propio producto");
        }

        if ( monto == null || monto <= 0 ) {
            throw new ErrorNegocio(400, "Escribe un monto valido");
        }

        if ( ofertas.estaVacia() && monto < producto.getPrecioBase() ) {
            throw new ErrorNegocio(400, "La primera puja debe ser igual o mayor al precio base");
        }

        if ( !ofertas.estaVacia() && monto <= ofertas.verMayor().getMonto() ) {
            throw new ErrorNegocio(400, "La puja debe ser mayor que la puja mas alta");
        }

    }

    @Override
    public int compareTo(Subasta otra) {

        return producto.getFechaFin().compareTo(otra.getProducto().getFechaFin());

    }

}
