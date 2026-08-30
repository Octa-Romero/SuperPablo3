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
import com.prz.juego.Principal;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

    private Principal juego;
    private Render render;
    private Nivel nivel;

    public PantallaJuego(Principal juego) {
        this.juego = juego;
        this.render = juego.getRender();
        this.nivel = new Nivel(juego);
    }

    @Override
    public void show() {
        nivel.show();
    }

    @Override
    public void render(float delta) {
        render.limpiarPantalla();
        nivel.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        nivel.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        nivel.dispose();
    }
}
