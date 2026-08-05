package com.prz.juego.pantallas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaMenu implements Screen {

    SpriteBatch batch;
    Imagen fondo;

    public PantallaMenu()
    {
        batch = Render.batch;
    }

    @Override
    public void show() {
        fondo = new Imagen("pabloFondo2.png");
        fondo.setSize(Config.ANCHO, Config.ALTO);
    }

    @Override
    public void render(float delta) {
        batch.begin();
        fondo.dibujar();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

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
        batch.dispose();
    }
}
