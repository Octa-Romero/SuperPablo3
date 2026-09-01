package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;

public class Walter extends Jugador {

	public Walter(float x, float y, float ancho, float alto) {
		super(x, y, ancho, alto, 200, 10, 1, new Texture("Personajes/walter.png"), new Texture("Hud/walter_hud.png"));
	}

	@Override
	public void atacar() {
		// Walter todavia no tiene ataque
	}
}
