package com.prz.juego.niveles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Pablo;
import com.prz.juego.entidades.Walter;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public abstract class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Colisiones colision;
    private ArrayList<Entidad> entidades = new ArrayList<>();
    private ShapeRenderer shapeRenderer;
    private boolean gameOver = false;

    public Nivel(Jugador jugador) {
        this.jugador = jugador;
    }

    public void cargar() {
        camara = new Camara();

        entidades.add(jugador);

        crearEntidades();

        shapeRenderer = new ShapeRenderer();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(getRutaMapa());

        colision = new Colisiones(mapa);

        configurarJugador();

        MapProperties props = mapa.getProperties();

        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;

        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);

        reproducirMusica();
    }

    private void configurarJugador() {
        Jugador jugador = getJugador();

        if (jugador instanceof Pablo) {
            ((Pablo) jugador).setEntidades(getEntidades());
        } else if (jugador instanceof Walter) {
            ((Walter) jugador).configurarAtaque(getEntidades(), getColision());
        }
    }

    protected abstract String getRutaMapa();

    protected abstract void crearEntidades();

    protected abstract void reproducirMusica();

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        for (Entidad entidad : entidades) {
            entidad.dibujar();

            if (entidad instanceof Enemigo) {
                ((Enemigo) entidad).dibujarAtaque();
            }
        }

        if (jugador instanceof Pablo) {
            ((Pablo) jugador).dibujarAtaque();
        }

        if (jugador instanceof Walter) {
            ((Walter) jugador).dibujarOrbes();
        }
    }

    public void renderDebug() {
        if (!Debug.mostrarHitboxes()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camara.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        colision.dibujarHitboxes(shapeRenderer);

        for (Entidad entidad : entidades) {
            entidad.dibujarHitbox(shapeRenderer);
        }

        if (jugador instanceof Walter) {
            ((Walter) jugador).dibujarOrbesHitbox(shapeRenderer);
        }

        shapeRenderer.end();
    }

    public void update(float delta, Entrada entrada) {

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

        camara.seguirPersonaje(jugador.getX() + jugador.getAncho() / 2f, jugador.getY() + jugador.getAlto() / 2f, delta);

        if (entrada.aprietaF1()) {
            Debug.cambiarHitboxes();
        }

        if (jugador.estaMuerto()) {
            gameOver = true;
        }
    }

    public void resize(int width, int height) {
        camara.actualizarTamano(width, height);
    }

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
    }

    public Camera getCamara() {
        return camara.getCamera();
    }

    public void guardarPosicionCamara() {
        camara.guardarPosicion();
    }

    public void restaurarPosicionCamara() {
        camara.restaurarPosicion();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    protected Jugador getJugador() {
        return jugador;
    }

    protected Colisiones getColision() {
        return colision;
    }

    protected ArrayList<Entidad> getEntidades() {
        return entidades;
    }
}
