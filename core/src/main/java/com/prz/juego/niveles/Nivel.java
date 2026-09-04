package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Entrada;

public class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Colisiones colision;

    public void cargar(String ruta) {

        camara = new Camara();

        jugador = new Jugador(50, 100, 55, 100);

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(ruta);

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

    public void render(SpriteBatch batch) {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
        jugador.dibujar(batch);
    }

    public void update(float delta, Entrada entrada)
    {
        jugador.update(entrada, delta);

        colision.chequearColision(jugador);

        camara.seguirPersonaje(
            jugador.getX() + jugador.getAncho() / 2f,
            jugador.getY() + jugador.getAlto() / 2f,
            delta
        );
    }

    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
    }


    public void dispose() {
        if (mapa != null) mapa.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    public Camera getCamara()
    {
        return this.camara.getCamera();
    }
}
