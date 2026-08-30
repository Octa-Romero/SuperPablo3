package com.prz.juego.niveles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.prz.juego.Principal;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class Nivel implements Screen {

    private Camara camara;
    private TiledMap mapa;
    private TmxMapLoader mapLoader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Entrada entrada;
    private Colisiones colision;
    private Principal juego;
    private Render render;


    public Nivel(Principal juego)
    {
        this.juego = juego;
        this.render = juego.getRender();
        jugador = new Jugador(50, 100, 55, 100);
        entrada = new Entrada();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(entrada);

        camara = new Camara();

        mapLoader = new TmxMapLoader();
        mapa = mapLoader.load("Niveless/Niveles/Level1.tmx");

        colision = new Colisiones(mapa);

        MapProperties props = mapa.getProperties();
        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;

        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);
    }

    @Override
    public void render(float delta) {
        update(delta);
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
        render.begin(camara.getCamera());
        jugador.dibujar(render.getBatch());
        render.end();
    }

    public void update(float delta)
    {
        jugador.update(entrada, delta);

        colision.chequearColision(jugador);

        camara.seguirPersonaje(
            jugador.getX() + jugador.getAncho() / 2f,
            jugador.getY() + jugador.getAlto() / 2f,
            delta
        );
    }

    @Override
    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (mapa != null) mapa.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
