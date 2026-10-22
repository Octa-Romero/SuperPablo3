
package com.prz.juego.utilidades;

public class Dialogo {

    private final String[] mensajes;
    private int indice;
    private boolean activo;

    public Dialogo(String ... mensajes) {
        this.mensajes = mensajes;
        indice = 0;
        activo = false;
    }

    public void iniciar() {
        if (mensajes.length > 0) {
            indice = 0;
            activo = true;
        }
    }

    public void avanzar() {
        if (!activo) {
            return;
        }

        indice++;

        if (indice >= mensajes.length) {
            activo = false;
        }
    }

    public String getMensajeActual() {
        if (!activo) {
            return null;
        }

        return mensajes[indice];
    }

    public boolean estaActivo() {
        return activo;
    }

    public void cerrar() {
        activo = false;
    }
}
