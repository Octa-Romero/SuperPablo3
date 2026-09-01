package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.entidades.Jugador;

public class Hud {

	private Jugador jugador;
	private SpriteBatch batch = new SpriteBatch();
	private OrthographicCamera camara = new OrthographicCamera();
	private Texture cara;
	private Texture corazonLleno = new Texture("corazon_lleno.png");;
	private Texture corazonMitad = new Texture("corazon_mitad.png");;
	private Texture corazonVacio = new Texture("corazon_vacio.png");;
	private float margen = 20;
	private float tamañoCara = 100;
	private float tamañoCorazon = 40;
	private float espacioCorazones = 5;

	public Hud(Jugador jugador, Texture cara) {
		this.jugador = jugador;
		this.cara = cara;
		actualizarCamara();
	}

	private void actualizarCamara() {
		camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camara.update();
	}

	public void dibujar() {
		actualizarCamara();
		batch.setProjectionMatrix(camara.combined);
		batch.begin();

		float x = margen;
		float y = Gdx.graphics.getHeight() - margen - tamañoCara;

		batch.draw(cara, x, y, tamañoCara, tamañoCara);

		float corazonesX = x + tamañoCara + 10;
		float corazonesY = y + (tamañoCara - tamañoCorazon) / 2f;
		double vida = jugador.getVida();
		double vidaMaxima = jugador.getVidaMaxima();
		int cantidadCorazones = (int) Math.ceil(vidaMaxima);

		for (int i = 0; i < cantidadCorazones; i++) {
			double vidaRestante = vida - i;
			Texture textura;

			if (vidaRestante >= 1.0) {
				textura = corazonLleno;
			} else if (vidaRestante >= 0.5) {
				textura = corazonMitad;
			} else {
				textura = corazonVacio;
			}

			batch.draw(textura, corazonesX + i * (tamañoCorazon + espacioCorazones), corazonesY, tamañoCorazon, tamañoCorazon);
		}

		batch.end();
	}

	public void dispose() {
		batch.dispose();
		cara.dispose();
		corazonLleno.dispose();
		corazonMitad.dispose();
		corazonVacio.dispose();
	}
}