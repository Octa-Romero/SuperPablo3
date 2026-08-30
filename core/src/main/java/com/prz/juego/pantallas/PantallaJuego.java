package com.prz.juego.pantallas;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Enemigo;
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
    private ShapeRenderer shapeRenderer;

    private Jugador jugador;
    private Entrada entrada;
    private Colisiones colision;
    private ArrayList<Enemigo> enemigos = new ArrayList<>();

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

        shapeRenderer = new ShapeRenderer();
        
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

        // A. Actualizar lógica
        update(delta);

        // B. Dibujar mapa
        mapRenderer.setView(camaraJuego.getCamera());
        mapRenderer.render();

        // C. Dibujar jugador y enemigos
        Render.batch.setProjectionMatrix(camaraJuego.getCamera().combined);
        Render.batch.begin();

        jugador.dibujar();

        Render.batch.end();

        // D. Dibujar enemigos
        shapeRenderer.setProjectionMatrix(camaraJuego.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Enemigo enemigo : enemigos) {
            enemigo.dibujar(shapeRenderer);
        }

        shapeRenderer.end();
    }

    private void update(float delta) {
        // 1. Mover al jugador con sus teclas (W, A, S, D)
        jugador.update(entrada, delta);

        colision.chequearColision(jugador);

        for(Enemigo enemigo : enemigos) {
        	enemigo.actualizar(delta);
        }
        
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
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
