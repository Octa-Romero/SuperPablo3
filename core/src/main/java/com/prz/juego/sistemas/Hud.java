package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class Hud {

	private final Jugador jugador;
	private final Stage stage;
	private final FitViewport viewport = new FitViewport(1280, 720, new OrthographicCamera());
    private final Texture corazonLleno = GestorRecursos.obtenerTextura("Hud/corazon_lleno.png");
	private final Texture corazonMitad = GestorRecursos.obtenerTextura("Hud/corazon_mitad.png");
	private final Texture corazonVacio = GestorRecursos.obtenerTextura("Hud/corazon_vacio.png");
	private Table tablaPrincipal;
	private Table contenedorCorazones;
	private final float TAMANO_CARA = 120;
	private final float TAMANO_CORAZON = 50;
	private final float ESPACIO_CORAZONES = 5;

	public Hud(Jugador jugador, SpriteBatch batch) {
		this.jugador = jugador;
		stage = new Stage(viewport, batch);
		tablaPrincipal = new Table();
		tablaPrincipal.top().left();
		tablaPrincipal.setFillParent(true);
		tablaPrincipal.pad(20);
		Image imagenCara = new Image(jugador.getTEXTURA_HUD());
		contenedorCorazones = new Table();
		tablaPrincipal.add(imagenCara).size(TAMANO_CARA, TAMANO_CARA);
		tablaPrincipal.add(contenedorCorazones).padLeft(10).center();
		stage.addActor(tablaPrincipal);
	}

	private void actualizarCorazones() {
		contenedorCorazones.clearChildren();

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

			Image corazon = new Image(textura);
			contenedorCorazones.add(corazon).size(TAMANO_CORAZON, TAMANO_CORAZON).padRight(ESPACIO_CORAZONES);
		}
	}

	public void dibujar() {
		actualizarCorazones();
		stage.act();
		stage.draw();
	}

	public void actualizarTamano(int width, int height) {
		viewport.update(width, height, true);
	}

	public void dispose() {
		stage.dispose();
	}
}
