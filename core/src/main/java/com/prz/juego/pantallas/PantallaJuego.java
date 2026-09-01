package com.prz.juego.pantallas;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Bicho1;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Pablo;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

	private TiledMap mapa;
	private TmxMapLoader mapLoader;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Camara camaraJuego;
	private Jugador jugador;
	private Entrada entrada;
	private Colisiones colision;
	private Hud hud;
	private ArrayList<Entidad> entidades = new ArrayList<>();
	private ShapeRenderer shapeRenderer;

	public PantallaJuego() {
		if (Render.batch == null) {
			Render.batch = new SpriteBatch();
		}
		shapeRenderer = new ShapeRenderer();
		entrada = new Entrada();
		Gdx.input.setInputProcessor(entrada);

		jugador = new Pablo(50, 150, 55, 100);
		jugador.setEntrada(entrada);
		entidades.add(jugador);
		((Pablo) jugador).setEntidades(entidades);

		hud = new Hud(jugador, jugador.getTexturaHud(), Render.batch);
	}

	@Override
	public void show() {
		camaraJuego = new Camara();

		mapLoader = new TmxMapLoader();
		mapa = mapLoader.load("Niveles/Niveles/Level1.tmx");

		colision = new Colisiones(mapa);

		entidades.add(new Bicho1(300, 200, jugador));
		entidades.add(new Bicho1(500, 200, jugador));

		MapProperties props = mapa.getProperties();

		int tileWidth = props.get("tilewidth", Integer.class);
		int tileHeight = props.get("tileheight", Integer.class);
		int mapWidthTiles = props.get("width", Integer.class);
		int mapHeightTiles = props.get("height", Integer.class);
		float mapWidthPixels = mapWidthTiles * tileWidth;
		float mapHeightPixels = mapHeightTiles * tileHeight;

		camaraJuego.setLimitesMapa(mapWidthPixels, mapHeightPixels);

		mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla();

		update(delta);

		mapRenderer.setView(camaraJuego.getCamera());
		mapRenderer.render();

		Render.batch.setProjectionMatrix(camaraJuego.getCamera().combined);
		Render.batch.begin();

		for (Entidad entidad : entidades) {
			entidad.dibujar();
			if (entidad instanceof Enemigo) {
				((Enemigo) entidad).dibujarAtaque();
			}
		}

		if (jugador instanceof Pablo) {
			((Pablo) jugador).dibujarAtaque();
		}

		Render.batch.end();

		if (Debug.mostrarHitboxes()) {
			shapeRenderer.setProjectionMatrix(camaraJuego.getCamera().combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

			colision.dibujarHitboxes(shapeRenderer);

			for (Entidad entidad : entidades) {
				entidad.dibujarHitbox(shapeRenderer);
			}

			shapeRenderer.end();
		}

		hud.dibujar();
	}

	private void update(float delta) {
		for (Entidad entidad : entidades) {
			entidad.update(delta);
			colision.chequearColision(entidad);
		}

		for (int i = entidades.size() - 1; i >= 0; i--) {
			Entidad entidad = entidades.get(i);
			if (entidad instanceof Enemigo) {
				Enemigo enemigo = (Enemigo) entidad;
				if (enemigo.estaMuerto()) {
					enemigo.dispose();
					entidades.remove(i);
				}
			}
		}

		camaraJuego.seguirPersonaje(jugador.getX() + jugador.getAncho() / 2f, jugador.getY() + jugador.getAlto() / 2f, delta);

		if (entrada.mostrarHitboxes()) {
			Debug.cambiarHitboxes();
		}
	}

	@Override
	public void resize(int width, int height) {
		camaraJuego.actualizarTamano(width, height);
		hud.actualizarTamano(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		if (mapa != null) {
			mapa.dispose();
		}

		if (mapRenderer != null) {
			mapRenderer.dispose();
		}

		if (shapeRenderer != null) {
			shapeRenderer.dispose();
		}

		for (Entidad entidad : entidades) {
			entidad.dispose();
		}

		if (hud != null) {
			hud.dispose();
		}
	}
}
