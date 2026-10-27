package com.prz.juego.niveles;

import com.prz.juego.entidades.jugadores.Jugador;

public class Nivel2 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Nivel2.1.tmx";

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    protected void configurarContenidoEspecifico(Jugador jugador) {
        // Este mapa todavía no define entidades propias del nivel.
    }
}
