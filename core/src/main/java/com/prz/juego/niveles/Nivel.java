
package com.prz.juego.niveles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Lanzable;
import com.prz.juego.entidades.Reliquia;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.enemigos.rango.EnemigoRango;
import com.prz.juego.entidades.jugadores.*;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Debug;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

import java.util.ArrayList;

public abstract class Nivel {

    private Camara camara;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Jugador jugador;
    private Colisiones colision;

    private final ArrayList<Entidad> ENTIDADES = new ArrayList<>();
    private final ArrayList<Lanzable> PROYECTILES = new ArrayList<>();
    private final ArrayList<Reliquia> RELIQUIAS = new ArrayList<>();

    private ShapeRenderer shapeRenderer;
    private boolean gameOver = false;
    private boolean tieneReliquia = false;
    private float anchoMapa;

    public static Nivel crear(String ruta) {
        switch (ruta) {
            case Nivel1.RUTA:
                return new Nivel1();
            case Nivel2.RUTA:
                return new Nivel2();
            case Nivel3.RUTA:
                return new Nivel3();
            case Nivel4.RUTA:
                return new Nivel4();
            case Nivel5.RUTA:
                return new Nivel5();
            case Nivel6.RUTA:
                return new Nivel6();
            case Nivel7.RUTA:
                return new Nivel7();
            default:
                throw new IllegalArgumentException(
                    "No existe una clase para el nivel: " + ruta
                );
        }
    }

    public final void cargar(Jugador jugador) {
        camara = new Camara();

        this.jugador = jugador;
        jugador.setX(getPosicionInicialX());
        jugador.setY(getPosicionInicialY());

        ENTIDADES.add(jugador);

        shapeRenderer = new ShapeRenderer();

        TmxMapLoader mapLoader = new TmxMapLoader();
        mapa = mapLoader.load(getRutaMapa());

        colision = new Colisiones(mapa);
        jugador.setColisiones(colision);

        if (jugador instanceof Pablo) {
            ((Pablo) jugador).setEntidades(ENTIDADES);
            ((Pablo) jugador).setProyectiles(PROYECTILES);
        } else {
            ((Walter) jugador).configurarAtaque(
                ENTIDADES, colision, PROYECTILES
            );
        }

        configurarContenidoEspecifico(jugador);

        MapProperties props = mapa.getProperties();

        int tileWidth = props.get("tilewidth", Integer.class);
        int tileHeight = props.get("tileheight", Integer.class);
        int mapWidthTiles = props.get("width", Integer.class);
        int mapHeightTiles = props.get("height", Integer.class);

        float mapTotalWidth = tileWidth * mapWidthTiles;
        float mapTotalHeight = tileHeight * mapHeightTiles;

        anchoMapa = mapTotalWidth;
        camara.setLimitesMapa(mapTotalWidth, mapTotalHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapa, 1f);

        Musica.NIVEL.sonar();
    }

    protected abstract String getRutaMapa();

    protected abstract void configurarContenidoEspecifico(Jugador jugador);

    protected float getPosicionInicialX() {
        return 50;
    }

    protected float getPosicionInicialY() {
        return 150;
    }

    protected final void agregarEntidad(Entidad entidad) {
        if (entidad instanceof EnemigoRango) {
            ((EnemigoRango) entidad).configurarDisparo(
                colision, PROYECTILES
            );
        }

        ENTIDADES.add(entidad);
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

        for (Lanzable proyectil : PROYECTILES) {
            proyectil.dibujar();
        }

        // Dibujar las reliquias dentro del mundo del juego.
        for (Reliquia reliquia : RELIQUIAS) {
            reliquia.dibujar();
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

        for (Lanzable proyectil : PROYECTILES) {
            proyectil.dibujarHitbox(shapeRenderer);
        }

        shapeRenderer.end();
    }

    public void update(float delta, Entrada entrada) {

        // Actualizar entidades.
        for (Entidad entidad : ENTIDADES) {
            entidad.update(delta);
            colision.chequearColision(entidad);
            entidad.limitarMovimientoHorizontal(0, anchoMapa);
        }

        // Actualizar proyectiles.
        for (int i = PROYECTILES.size() - 1; i >= 0; i--) {
            Lanzable proyectil = PROYECTILES.get(i);
            proyectil.update(delta);

            if (!proyectil.estaActiva()) {
                PROYECTILES.remove(i);
            }
        }

        // Eliminar enemigos muertos y generar la reliquia si corresponde.
        for (int i = ENTIDADES.size() - 1; i >= 0; i--) {
            Entidad entidad = ENTIDADES.get(i);

            if (entidad instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) entidad;

                if (enemigo.estaMuerto()) {

                    if (enemigo.sueltaReliquia()) {
                        System.out.println(
                            "[RELIQUIA] ¡El enemigo marcado murió!"
                        );

                        float xReliquia = enemigo.getX()
                            + (enemigo.getAncho() - 32f) / 2f;

                        float yReliquia = enemigo.getY();

                        System.out.println(
                            "[RELIQUIA] Posición: X="
                                + xReliquia + ", Y=" + yReliquia
                        );

                        RELIQUIAS.add(
                            new Reliquia(xReliquia, yReliquia, colision)
                        );

                        System.out.println(
                            "[RELIQUIA] Reliquia creada. Total: "
                                + RELIQUIAS.size()
                        );
                    }

                    enemigo.dispose();
                    ENTIDADES.remove(i);
                }
            }
        }

        // Actualizar y recoger reliquias.
        for (int i = RELIQUIAS.size() - 1; i >= 0; i--) {
            Reliquia reliquia = RELIQUIAS.get(i);

            if (reliquia.actualizar(jugador, delta)) {
                tieneReliquia = true;

                reliquia.dispose();
                RELIQUIAS.remove(i);
            }
        }

        // Actualizar cámara.
        camara.seguirPersonaje(
            jugador.getX() + jugador.getAncho() / 2f,
            jugador.getY() + jugador.getAlto() / 2f,
            delta
        );

        // Alternar hitboxes.
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

        for (Entidad entidad : ENTIDADES) {
            entidad.dispose();
        }

        ENTIDADES.clear();
        PROYECTILES.clear();

        for (Reliquia reliquia : RELIQUIAS) {
            reliquia.dispose();
        }

        RELIQUIAS.clear();
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
        return this.gameOver;
    }

    public boolean tieneReliquia() {
        return tieneReliquia;
    }
}
