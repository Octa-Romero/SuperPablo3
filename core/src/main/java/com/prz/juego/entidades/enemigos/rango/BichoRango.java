package com.prz.juego.entidades.enemigos.rango;

import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class BichoRango extends EnemigoRango {

    public BichoRango(float x, float y, Jugador jugador, Modo modo) {
        super(
                x,
                y,
                70,
                110,
                100,
                3,
                0.5,
                jugador,
                GestorRecursos.obtenerTextura("Enemigos/BichoRango/bichoRango.png"),
                GestorRecursos.obtenerTextura("Enemigos/BichoRango/flecha.png"),
                50,
                10,
                modo);
        vidaProyectil = 0.5;
    }
}
