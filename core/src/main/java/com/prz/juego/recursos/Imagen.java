package com.prz.juego.recursos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.utilidades.Render;

public class Imagen {

    private Texture t;
    private Sprite s;

    public Imagen(String ruta)
    {
        t = new Texture(ruta);
        s = new Sprite(t);
    }

    public void dibujar(SpriteBatch batch)
    {
        s.setPosition(0, 0);
        s.draw(batch);
    }

    public void setSize(float ancho, float alto)
    {
        s.setSize(ancho, alto);
    }
}
