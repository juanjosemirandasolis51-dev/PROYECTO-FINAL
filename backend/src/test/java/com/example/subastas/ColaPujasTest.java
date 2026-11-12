package com.example.subastas;

import com.example.subastas.Estructura.ColaPujas;
import com.example.subastas.Model.Entity.Puja;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ColaPujasTest {

    private Puja puja(long id, double monto) {

        return new Puja(id, 1L, 1L, monto, LocalDateTime.now());

    }

    @Test
    public void colaVaciaDevuelveNull() {

        ColaPujas cola = new ColaPujas();

        assertTrue(cola.estaVacia());
        assertNull(cola.verMayor());
        assertNull(cola.sacarMayor());

    }

    @Test
    public void laMayorQuedaArriba() {

        ColaPujas cola = new ColaPujas();

        cola.agregar(puja(1, 5000));
        cola.agregar(puja(2, 9000));
        cola.agregar(puja(3, 7000));

        assertEquals(9000, cola.verMayor().getMonto());
        assertEquals(3, cola.tamano());

    }

    @Test
    public void sacaEnOrdenDeMayorAMenor() {

        ColaPujas cola = new ColaPujas();
        double[] montos = {300, 100, 900, 500, 700, 200, 800};

        for ( int i = 0; i < montos.length; i++ ) {
            cola.agregar(puja(i, montos[i]));
        }

        assertEquals(900, cola.sacarMayor().getMonto());
        assertEquals(800, cola.sacarMayor().getMonto());
        assertEquals(700, cola.sacarMayor().getMonto());
        assertEquals(500, cola.sacarMayor().getMonto());
        assertEquals(300, cola.sacarMayor().getMonto());
        assertEquals(200, cola.sacarMayor().getMonto());
        assertEquals(100, cola.sacarMayor().getMonto());
        assertTrue(cola.estaVacia());

    }

    @Test
    public void creceCuandoSeLlena() {

        ColaPujas cola = new ColaPujas();

        for ( int i = 1; i <= 100; i++ ) {
            cola.agregar(puja(i, i * 10));
        }

        assertEquals(100, cola.tamano());
        assertEquals(1000, cola.verMayor().getMonto());

    }

}
