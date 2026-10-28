package com.prz.juego.entidades.enemigos.melee;

import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class BichoMelee extends EnemigoMelee {

    public BichoMelee(float x, float y, Jugador jugador) {
        super(
                x,
                y,
                70,
                110,
                100,
                3,
                0.5,
                jugador,
                GestorRecursos.obtenerTextura("Enemigos/BichoMelee/bichoMelee.png"),
                GestorRecursos.obtenerTextura("Enemigos/BichoMelee/espada.png"));
    }
}
