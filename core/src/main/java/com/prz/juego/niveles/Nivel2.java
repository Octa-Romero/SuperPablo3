package com.prz.juego.niveles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.sistemas.Camara;

public class Nivel2 implements Screen {

	private TiledMap map;
	private TmxMapLoader mapLoader;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Camara camaraJuego;

	// Posición inicial del punto de prueba
	// Cambiá estas líneas en Level2Screen.java:

	private float playerX = 500f; // <-- Empieza más adelante (supera el límite de 240px)
	private float playerY = 150f;

	private static final float SPEED = 600f; // <-- Subir velocidad para probar rápido

	@Override
	public void show() {
		camaraJuego = new Camara();

		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Niveless/Niveles/Nivel2.tmx");


		MapProperties props = map.getProperties();
		int tileWidth = props.get("tilewidth", Integer.class);
		int tileHeight = props.get("tileheight", Integer.class);
		int mapWidthTiles = props.get("width", Integer.class);
		int mapHeightTiles = props.get("height", Integer.class);

		float mapWidthPixels = mapWidthTiles * tileWidth;
		float mapHeightPixels = mapHeightTiles * tileHeight;

		camaraJuego.setLimitesMapa(mapWidthPixels, mapHeightPixels);

		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		update(delta);

		mapRenderer.setView(camaraJuego.getCamera());
		mapRenderer.render();
	}

	private void update(float delta) {
		// Actualizar el seguimiento de la cámara a la nueva posición
		camaraJuego.seguirPersonaje(playerX, playerY, delta);
	}

	@Override
	public void resize(int width, int height) {
		camaraJuego.actualizarTamano(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		if (map != null) map.dispose();
		if (mapRenderer != null) mapRenderer.dispose();
	}
}