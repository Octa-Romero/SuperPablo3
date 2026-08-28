package com.prz.juego.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class Jugador {

    public Texture textura;
    public Sprite sprite;
    private float x, y;
    private float ancho, alto;
    private int velocidadX = 0;
    private int velocidadY = 0;
    private final int gravedad = 400;
    private boolean enSuelo = true;
    private Rectangle bounds = new Rectangle();

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

    public void update(Entrada entrada, float delta)
    {
        if (entrada.mueveIzquierda()) velocidadX = -200;
        else if (entrada.mueveDerecha()) velocidadX = 200;
        else velocidadX = 0;

        if (entrada.mueveArriba()) {
            saltar();
        }

        velocidadY -= gravedad * delta;

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
        bounds.set(x, y, ancho, alto);
        return this.bounds;
    }

    public int getVelocidadX() {
        return velocidadX;
    }

    public int getVelocidadY() {return velocidadY;}

    public void setVelocidadX(int valor) {this.velocidadX = valor;}

    public void setVelocidadY(int valor) {this.velocidadY = valor;}

    public void setX(float x)
    {
        this.x = x;
        sprite.setX(x);
    }

    public void setY(float y)
    {
        this.y = y;
        sprite.setY(y);
    }

    public float getAncho() {
        return this.ancho;
    }

    public float getAlto()
    {
        return this.alto;
    }
}
