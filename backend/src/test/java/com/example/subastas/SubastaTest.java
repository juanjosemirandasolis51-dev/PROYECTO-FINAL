package com.example.subastas;

import com.example.subastas.Excepcion.ErrorNegocio;
import com.example.subastas.Model.Entity.Producto;
import com.example.subastas.Model.Entity.Puja;
import com.example.subastas.Model.Subasta;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class SubastaTest {

    private Subasta nueva(int minutos) {

        Producto p = new Producto(1L, 10L, "Bicicleta", "Casi nueva", 1000, LocalDateTime.now(), LocalDateTime.now().plusMinutes(minutos), "ACTIVA");

        return new Subasta(p);

    }

    @Test
    public void primeraPujaIgualAlPrecioBaseSeAcepta() {

        Subasta s = nueva(10);

        assertDoesNotThrow(() -> s.validarPuja(20L, 1000.0));

    }

    @Test
    public void primeraPujaMenorAlPrecioBaseSeRechaza() {

        Subasta s = nueva(10);

        ErrorNegocio e = assertThrows(ErrorNegocio.class, () -> s.validarPuja(20L, 900.0));
        assertEquals(400, e.getCodigo());

    }

    @Test
    public void pujaIgualALaMasAltaSeRechaza() {

        Subasta s = nueva(10);
        s.agregarPuja(new Puja(1L, 1L, 20L, 1500, LocalDateTime.now()));

        assertThrows(ErrorNegocio.class, () -> s.validarPuja(30L, 1500.0));
        assertDoesNotThrow(() -> s.validarPuja(30L, 1501.0));

    }

    @Test
    public void elVendedorNoPuedePujar() {

        Subasta s = nueva(10);

        ErrorNegocio e = assertThrows(ErrorNegocio.class, () -> s.validarPuja(10L, 5000.0));
        assertEquals(403, e.getCodigo());

    }

    @Test
    public void noSePuedePujarEnSubastaCerrada() {

        Subasta s = nueva(10);
        s.getProducto().setEstado("CERRADA");

        ErrorNegocio e = assertThrows(ErrorNegocio.class, () -> s.validarPuja(20L, 5000.0));
        assertEquals(400, e.getCodigo());

    }

    @Test
    public void precioActualYHistorial() {

        Subasta s = nueva(10);

        assertEquals(1000, s.getPrecioActual());

        s.agregarPuja(new Puja(1L, 1L, 20L, 1200, LocalDateTime.now()));
        s.agregarPuja(new Puja(2L, 1L, 30L, 1800, LocalDateTime.now()));

        assertEquals(1800, s.getPrecioActual());
        assertEquals(30L, s.getMejorPuja().getUsuarioId());
        assertEquals(2, s.getHistorial().size());
        assertEquals(1200, s.getHistorial().get(0).getMonto());

    }

    @Test
    public void laColaDeCierresDejaArribaLaQueTerminaPrimero() {

        PriorityQueue<Subasta> cierres = new PriorityQueue<>();

        Subasta larga = nueva(30);
        Subasta corta = nueva(5);
        Subasta media = nueva(15);

        cierres.add(larga);
        cierres.add(corta);
        cierres.add(media);

        assertEquals(corta, cierres.poll());
        assertEquals(media, cierres.poll());
        assertEquals(larga, cierres.poll());

    }

}
