package com.prz.juego.niveles;

import com.prz.juego.entidades.enemigos.melee.BichoMelee;
import com.prz.juego.entidades.jugadores.Jugador;

public class Nivel1 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Level1.tmx";

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    protected void configurarContenidoEspecifico(Jugador jugador) {
        agregarEntidad(new BichoMelee(2000, 300, jugador));
        agregarEntidad(new BichoMelee(2200, 300, jugador));
    }
}
