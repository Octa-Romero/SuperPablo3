package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.entidades.Jugador;

public class Hud {

	private Jugador jugador;
	private Stage stage;
	private FitViewport viewport = new FitViewport(1280, 720, new OrthographicCamera());;
	private Texture cara;
	private Texture corazonLleno = new Texture("corazon_lleno.png");;
	private Texture corazonMitad = new Texture("corazon_mitad.png");;
	private Texture corazonVacio = new Texture("corazon_vacio.png");;
	private Table tablaPrincipal;
	private Table contenedorCorazones;
	private float tamañoCara = 120;
	private float tamañoCorazon = 50;
	private float espacioCorazones = 5;

	public Hud(Jugador jugador, Texture cara, SpriteBatch batch) {
		this.jugador = jugador;
		this.cara = cara;
		stage = new Stage(viewport, batch);
		tablaPrincipal = new Table();
		tablaPrincipal.top().left();
		tablaPrincipal.setFillParent(true);
		tablaPrincipal.pad(20);
		Image imagenCara = new Image(cara);
		contenedorCorazones = new Table();
		tablaPrincipal.add(imagenCara).size(tamañoCara, tamañoCara);
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
			contenedorCorazones.add(corazon).size(tamañoCorazon, tamañoCorazon).padRight(espacioCorazones);
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
		cara.dispose(); 
		corazonLleno.dispose();
		corazonMitad.dispose();
		corazonVacio.dispose();
	}
}