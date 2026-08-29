package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
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

    public PantallaJuego() {
        if (Render.batch == null) {
            Render.batch = new SpriteBatch();
        }
        entrada = new Entrada();
        Gdx.input.setInputProcessor(entrada);
        jugador = new Jugador(50, 150, 55, 100);

    }

    @Override
    public void show() {
        camaraJuego = new Camara();

        // 1. Cargar mapa TMX
        mapLoader = new TmxMapLoader();
        mapa = mapLoader.load("Niveless/Niveles/Level1.tmx");

        colision = new Colisiones(mapa);


        // 2. Definir límites de cgámara según el tamaño del mapa
        MapProperties props = mapa.getProperties();
        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapWidthPixels = mapWidthTiles * tileWidth;
        float mapHeightPixels = mapHeightTiles * tileHeight;

        camaraJuego.setLimitesMapa(mapWidthPixels, mapHeightPixels);

        // 3. Renderer del mapa
        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        // A. Actualizar lógica de juego y cámara
        update(delta);

        // B. Decirle al mapa que se renderice usando la vista de la cámara
        mapRenderer.setView(camaraJuego.getCamera());
        mapRenderer.render();

        // C. Dibujar al jugador proyectado con la cámara del juego
        Render.batch.setProjectionMatrix(camaraJuego.getCamera().combined);
        Render.batch.begin();
        jugador.dibujar();
        Render.batch.end();
    }

    private void update(float delta) {
        // 1. Mover al jugador con sus teclas (W, A, S, D)
        jugador.update(entrada, delta);

        colision.chequearColision(jugador);

        camaraJuego.seguirPersonaje(
            jugador.getX() + jugador.getAncho() / 2f,
            jugador.getY() + jugador.getAlto() / 2f,
            delta
        );
    }



    @Override
    public void resize(int width, int height) {
        camaraJuego.actualizarTamano(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (mapa != null) mapa.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
