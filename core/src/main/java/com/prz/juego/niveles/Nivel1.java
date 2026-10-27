package com.prz.juego.niveles;

import com.prz.juego.entidades.enemigos.Bicho1;
import com.prz.juego.entidades.jugadores.Jugador;

public class Nivel1 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Level1.tmx";

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    protected void configurarContenidoEspecifico(Jugador jugador) {
        agregarEntidad(new Bicho1(2000, 300, jugador));
        agregarEntidad(new Bicho1(2200, 300, jugador));
    }
}
