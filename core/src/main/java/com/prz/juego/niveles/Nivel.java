package com.prz.juego.niveles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.*;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

public class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Colisiones colision;
    private final ArrayList<Entidad> ENTIDADES = new ArrayList<>();
    private ShapeRenderer shapeRenderer;
    private boolean gameOver = false;

    public void cargar(String ruta, Jugador jugador) {
        camara = new Camara();

        this.jugador = jugador;

        ENTIDADES.add(jugador);
        ENTIDADES.add(new Bicho1(2000, 300, jugador));
        ENTIDADES.add(new Bicho1(2200, 300, jugador));

        shapeRenderer = new ShapeRenderer();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(ruta);

        colision = new Colisiones(mapa);
        jugador.setColisiones(colision);

        if(jugador instanceof Pablo){
            ((Pablo) jugador).setEntidades(ENTIDADES);
        } else {
            ((Walter) jugador).configurarAtaque(ENTIDADES, colision);
        }

        MapProperties props = mapa.getProperties();

        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;
        System.out.println("Ancho real del mapa: " + mapTotalWidth);

        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);

        Musica.NIVEL1.sonar();
    }

    public void renderMapa() {
        mapRenderer.setView(camara.getCamera());
        mapRenderer.render();
    }

    public void render() {
        Render.batch.setProjectionMatrix(camara.getCamera().combined);

        for (Entidad entidad : ENTIDADES) {
            entidad.dibujar();

            if (entidad instanceof Enemigo) {
                ((Enemigo) entidad).dibujarAtaque();
            }
        }

        if (jugador instanceof Pablo) {
            ((Pablo) jugador).dibujarAtaque();
        }

        if (jugador instanceof Walter) {
            ((Walter) jugador).dibujarBolas();
        }
    }

    public void renderDebug() {
        if (!Debug.mostrarHitboxes()) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camara.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        colision.dibujarHitboxes(shapeRenderer);

        for (Entidad entidad : ENTIDADES) {
            entidad.dibujarHitbox(shapeRenderer);
        }

        if (jugador instanceof Walter) {
            ((Walter) jugador).dibujarBolasHitbox(shapeRenderer);
        }

        shapeRenderer.end();
    }

    public void update(float delta, Entrada entrada) {

        // Actualizar ENTIDADES
        for (Entidad entidad : ENTIDADES) {
            entidad.update(delta);
            colision.chequearColision(entidad);
        }

        // Eliminar enemigos muertos
        for (int i = ENTIDADES.size() - 1; i >= 0; i--) {
            Entidad entidad = ENTIDADES.get(i);

            if (entidad instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) entidad;

                if (enemigo.estaMuerto()) {
                    enemigo.dispose();
                    ENTIDADES.remove(i);
                }
            }
        }

        // Cámara
        camara.seguirPersonaje(
            jugador.getX() + jugador.getAncho() / 2f,
            jugador.getY() + jugador.getAlto() / 2f,
            delta
        );

        // Alternar hitboxes
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

        for(Entidad e : ENTIDADES)
        {
            e.dispose();
        }
        ENTIDADES.clear();
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

    public boolean isGameOver(){return this.gameOver;}
}
