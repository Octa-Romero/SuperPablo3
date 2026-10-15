package com.prz.juego.niveles;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.prz.juego.utilidades.Render;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Pablo;
import com.prz.juego.entidades.Walter;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.awt.*;
import java.util.ArrayList;

public class Hub {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Colisiones colision;
    private ShapeRenderer shapeRenderer;
    private Rectangle centroHub;
    private BitmapFont fontAlerta;

    private final ArrayList<Entidad> entidades = new ArrayList<>();

    public void cargar(String ruta, Jugador jugador) {
        camara = new Camara();
        this.jugador = jugador;
        shapeRenderer = new ShapeRenderer();

        inicializarFuenteAlerta();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(ruta);

        for (MapObject object : mapa.getLayers().get("centroHub").getObjects()){
            if(object instanceof RectangleMapObject){
                centroHub = ((RectangleMapObject) object).getRectangle();

            }
        }

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

    private void inicializarFuenteAlerta() {
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("Fuentes/alagard.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 28;
        parametro.color = Color.WHITE;
        parametro.shadowOffsetX = 2;
        parametro.shadowOffsetY = 2;
        parametro.shadowColor = new Color(0, 0, 0, 0.9f);

        fontAlerta = generator.generateFont(parametro);

        generator.dispose();
    }

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

        jugador.dibujar();

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

        jugador.update(delta);
        colision.chequearColision(jugador);

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
        if (mapa != null) {
            mapa.dispose();
        }

        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        fontAlerta.dispose();
    }

    public boolean estaEnCentroHub() {

        if (centroHub == null) {
            return false;
        }

        return centroHub.overlaps(jugador.getBounds());
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

    public void dibujarAlertaCentro() {
        if (!estaEnCentroHub()) {
            return;
        }

        String mensaje = "PRESIONA E PARA SELECCIONAR NIVEL";

        GlyphLayout layout = new GlyphLayout(fontAlerta, mensaje);

        float x = (getCamara().viewportWidth - layout.width) / 2f;
        float y = 80;

        fontAlerta.draw(Render.batch, mensaje, x, y);
    }
}
