package com.example.subastas.Estructura;

import com.example.subastas.Model.Entity.Puja;

public class ColaPujas {

    private Puja[] datos;
    private int tamano;

    public ColaPujas() {

        datos = new Puja[10];
        tamano = 0;

    }

    public void agregar(Puja puja) {

        if ( tamano == datos.length ) {
            Puja[] nuevo = new Puja[datos.length * 2];
            for ( int i = 0; i < tamano; i++ ) {
                nuevo[i] = datos[i];
            }
            datos = nuevo;
        }

        datos[tamano] = puja;
        int hijo = tamano;
        tamano++;

        while ( hijo > 0 ) {
            int padre = (hijo - 1) / 2;
            if ( datos[hijo].getMonto() > datos[padre].getMonto() ) {
                cambiar(hijo, padre);
                hijo = padre;
            }
            else {
                break;
            }
        }

    }

    public Puja verMayor() {

        if ( tamano == 0 ) { return null; }

        return datos[0];

    }

    public Puja sacarMayor() {

        if ( tamano == 0 ) { return null; }

        Puja mayor = datos[0];
        tamano--;
        datos[0] = datos[tamano];
        datos[tamano] = null;

        int padre = 0;

        while ( true ) {
            int izquierdo = padre * 2 + 1;
            int derecho = padre * 2 + 2;
            int grande = padre;

            if ( izquierdo < tamano && datos[izquierdo].getMonto() > datos[grande].getMonto() ) {
                grande = izquierdo;
            }
            if ( derecho < tamano && datos[derecho].getMonto() > datos[grande].getMonto() ) {
                grande = derecho;
            }
            if ( grande == padre ) {
                break;
            }

            cambiar(padre, grande);
            padre = grande;
        }

        return mayor;

    }

    public int tamano() {

        return tamano;

    }

    public boolean estaVacia() {

        return tamano == 0;

    }

    private void cambiar(int a, int b) {

        Puja temporal = datos[a];
        datos[a] = datos[b];
        datos[b] = temporal;

    }

}
