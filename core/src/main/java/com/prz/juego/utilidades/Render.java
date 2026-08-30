package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Render {

    private SpriteBatch batch;

    public Render(SpriteBatch batch) {
        this.batch = batch;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
    public void limpiarPantalla()
    {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    public void begin(Camera camara) {
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
    }

    public void end() {
        batch.end();
    }
}
