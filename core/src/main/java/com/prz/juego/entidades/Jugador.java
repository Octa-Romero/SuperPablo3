package com.prz.juego.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.utilidades.Render;

public class Jugador {

    public Texture textura;
    public Sprite sprite;
    private float x, y;
    private float ancho, alto;
    private final int velocidad = 200;
    private int velocidadY = 0;
    private final int gravedad = 350;
    private boolean enSuelo = true;

    public Jugador(float x, float y, float ancho, float alto)
    {
        textura = new Texture("walter.png");
        sprite = new Sprite(textura);
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        sprite.setPosition(x, y);
        sprite.setSize(ancho, alto);
    }

    public void dibujar()
    {
        sprite.draw(Render.batch);
    }

    public void update(float delta)
    {
        if (!enSuelo) {
            velocidadY -= gravedad * delta;
        }

        y += velocidadY * delta;

        if (y <= 0) {
            y = 0;
            velocidadY = 0;
            enSuelo = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && x >= 0) {
            x -= velocidad * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && x + ancho <= Gdx.graphics.getWidth()) {
            x += velocidad * delta;
        }

        sprite.setPosition(x, y);
    }

    public void saltar(){
        if (enSuelo) {
            velocidadY = 300;
            enSuelo = false;
        }
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public void setX(float x)
    {
        sprite.setX(x);
    }

    public void setY(float y)
    {
        sprite.setY(y);
    }

    public float getAncho()
    {
        return this.ancho;
    }

    public float getAlto()
    {
        return this.alto;
    }

}
