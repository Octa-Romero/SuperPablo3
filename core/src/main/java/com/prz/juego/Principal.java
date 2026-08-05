package com.prz.juego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.pantallas.PantallaMenu;
import com.prz.juego.utilidades.Render;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Principal extends Game {

    @Override
    public void create() {
        Render.batch = new SpriteBatch();
        this.setScreen(new PantallaMenu());
    }

    @Override
    public void dispose() {
        Render.batch.dispose();
    }
}
