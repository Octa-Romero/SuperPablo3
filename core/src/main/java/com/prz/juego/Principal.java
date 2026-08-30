package com.prz.juego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.pantallas.PantallaJuego;
import com.prz.juego.pantallas.PantallaMenu;
import com.prz.juego.utilidades.Render;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Principal extends Game {

    private Render render;
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        render = new Render(batch);
        this.setScreen(new PantallaMenu(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public Render getRender()
    {
        return render;
    }
}
