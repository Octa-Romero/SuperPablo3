package com.prz.juego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.pantallas.PantallaJuego;
import com.prz.juego.pantallas.PantallaMenu;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public class Principal extends Game {

	@Override
	public void create() {
		Render.batch = new SpriteBatch();
		this.setScreen(new PantallaMenu());
	}

	@Override
	public void dispose() {
		Screen pantallaActual = getScreen();
		setScreen(null);
		if (pantallaActual != null) {
			pantallaActual.dispose();
		}

		Musica.dispose();
		Sonido.dispose();

		if (Render.batch != null) {
			Render.batch.dispose();
			Render.batch = null;
		}
	}
}
