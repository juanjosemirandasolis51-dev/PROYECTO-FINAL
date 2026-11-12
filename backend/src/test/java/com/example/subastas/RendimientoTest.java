package com.example.subastas;

import com.example.subastas.Estructura.ColaPujas;
import com.example.subastas.Model.Entity.Puja;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class RendimientoTest {

    private final int cantidad = 100000;

    @Test
    public void mayorPujaConColaContraLista() {

        Random random = new Random(1);
        ColaPujas cola = new ColaPujas();
        List<Puja> lista = new ArrayList<>();

        for ( int i = 0; i < cantidad; i++ ) {
            Puja p = new Puja((long) i, 1L, 1L, random.nextInt(1000000), LocalDateTime.now());
            cola.agregar(p);
            lista.add(p);
        }

        long inicio = System.nanoTime();
        Puja mayorCola = null;
        for ( int i = 0; i < 1000; i++ ) {
            mayorCola = cola.verMayor();
        }
        long tiempoCola = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        Puja mayorLista = null;
        for ( int i = 0; i < 1000; i++ ) {
            mayorLista = lista.get(0);
            for ( Puja p : lista ) {
                if ( p.getMonto() > mayorLista.getMonto() ) { mayorLista = p; }
            }
        }
        long tiempoLista = System.nanoTime() - inicio;

        log.info("Buscar la puja mas alta 1000 veces entre {} pujas", cantidad);
        log.info("Con la cola de prioridad propia: {} ms", tiempoCola / 1000000.0);
        log.info("Recorriendo un ArrayList: {} ms", tiempoLista / 1000000.0);

        assertEquals(mayorLista.getMonto(), mayorCola.getMonto());
        assertTrue(tiempoCola < tiempoLista);

    }

    @Test
    public void buscarPorIdConMapaContraLista() {

        Map<Long, Puja> mapa = new HashMap<>();
        List<Puja> lista = new ArrayList<>();

        for ( long i = 0; i < cantidad; i++ ) {
            Puja p = new Puja(i, 1L, 1L, 1000, LocalDateTime.now());
            mapa.put(i, p);
            lista.add(p);
        }

        long buscado = cantidad - 1;

        long inicio = System.nanoTime();
        Puja enMapa = null;
        for ( int i = 0; i < 1000; i++ ) {
            enMapa = mapa.get(buscado);
        }
        long tiempoMapa = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        Puja enLista = null;
        for ( int i = 0; i < 1000; i++ ) {
            for ( Puja p : lista ) {
                if ( p.getId() == buscado ) {
                    enLista = p;
                    break;
                }
            }
        }
        long tiempoLista = System.nanoTime() - inicio;

        log.info("Buscar un elemento por id 1000 veces entre {}", cantidad);
        log.info("Con HashMap: {} ms", tiempoMapa / 1000000.0);
        log.info("Recorriendo un ArrayList: {} ms", tiempoLista / 1000000.0);

        assertEquals(enLista.getId(), enMapa.getId());
        assertTrue(tiempoMapa < tiempoLista);

    }

}
