package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Entrada;
import com.badlogic.gdx.math.Rectangle;

public class Jugador {

    public Imagen sprite;
    private float x, y;
    private float ancho, alto;
    private int velocidadX = 200;
    private int velocidadY = 0;
    private final int GRAVEDAD = 400;
    private boolean enSuelo = true;
    private Rectangle bounds = new Rectangle();

    public Jugador(float x, float y, float ancho, float alto)
    {
        sprite = new Imagen("walter.png");
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.bounds = new Rectangle(x, y, ancho, alto);
        sprite.setPosition(x, y);
        sprite.setSize(ancho, alto);
    }

    public void dibujar(SpriteBatch batch)
    {
        sprite.dibujar(batch);
    }

    public void update(Entrada entrada, float delta)
    {
        if (!enSuelo)
        {
            velocidadY -= GRAVEDAD * delta;
        }

        y += velocidadY * delta;

        if (entrada.mueveIzquierda())
        {
            x -= velocidadX * delta;
        }

        if (entrada.mueveDerecha())
        {
            x += velocidadX * delta;
        }

        if(entrada.mueveArriba())
        {
            saltar();
        }

        sprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }

    private void saltar(){
        if (enSuelo) {
            velocidadY = 350;
            enSuelo = false;
        }
    }

    public void setEnSuelo(boolean valor)
    {
        this.enSuelo = valor;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Rectangle getBounds()
    {
        return this.bounds;
    }

    public void setX(float x)
    {
        this.x = x;
        sprite.setX(x);
        bounds.setX(x);
    }

    public void setY(float y)
    {
        this.y = y;
        sprite.setY(y);
        bounds.setY(y);
    }

    public float getAncho() {
        return this.ancho;
    }

    public float getAlto()
    {
        return this.alto;
    }
}
