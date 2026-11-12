package com.example.subastas.Excepcion;

public class ErrorNegocio extends RuntimeException {

    private int codigo;

    public ErrorNegocio(int codigo, String mensaje) {

        super(mensaje);
        this.codigo = codigo;

    }

    public int getCodigo() {
        return codigo;
    }

}
