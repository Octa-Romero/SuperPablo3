package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Bicho1 extends Enemigo {

	public Bicho1(float x, float y, Jugador jugador) {
		super(x, y, 70, 110, 100, 3, 0.5, jugador, new Texture("Enemigos/Bicho1/bicho1.png"), new Texture("Enemigos/Bicho1/ataque_bicho1.png"));
	}
}
