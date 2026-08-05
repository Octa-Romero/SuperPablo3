package com.prz.juego.pantallas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.Principal;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

    Principal juego;
    Jugador jugador;

    public PantallaJuego(Principal juego)
    {
        this.juego = juego;
        Render.batch = new SpriteBatch();
        jugador = new Jugador(50, 100, 55, 100);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        jugador.update(delta);

        Render.batch.begin();
        jugador.dibujar();
        Render.batch.end();
    }


    @Override
    public void dispose() {
        Render.batch.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
