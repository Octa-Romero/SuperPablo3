package com.prz.juego.niveles;

import com.prz.juego.entidades.Bicho1;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Pablo;
import com.prz.juego.entidades.Walter;
import com.prz.juego.utilidades.Musica;

public class Nivel1 extends Nivel {

    public Nivel1(Jugador jugador) {
        super(jugador);
    }

    @Override
    protected String getRutaMapa() {
        return "Niveles/Niveles/Level1.tmx";
    }

    @Override
    protected void crearEntidades() {
        Jugador jugador = getJugador();

        getEntidades().add(new Bicho1(2000, 300, jugador));
        getEntidades().add(new Bicho1(2200, 300, jugador));
    }

    @Override
    protected void reproducirMusica() {
        Musica.NIVEL1.sonar();
    }
}
