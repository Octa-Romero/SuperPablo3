package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Bicho1 extends Enemigo {

	public Bicho1(float x, float y, Jugador jugador) {
		super(x, y, 55, 120, 100, 3, 0.5, jugador, new Texture("Enemigos/bicho1.png"), new Texture("Enemigos/ataque_bicho1.png"));
	}
}
