package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class Jugador {

    public Texture textura;
    public Sprite sprite;

    private float x, y;
    private float ancho, alto;

    private int velocidadX = 200;
    private int velocidadY = 0;

    private final int gravedad = 400;

    private boolean enSuelo = true;

    private Rectangle bounds = new Rectangle();
    private Texture[] texturasCaminar;
    private TextureRegion[] framesCaminar;
    private Animation<TextureRegion> animacionCaminar;
    private float tiempoAnimacion = 0f;

    private boolean mirandoDerecha = true;


    public Jugador(float x, float y, float ancho, float alto)
    {

        textura = new Texture("personaje/walter/13.png");


        texturasCaminar = new Texture[3];
        framesCaminar = new TextureRegion[3];

        texturasCaminar[0] = new Texture("personaje/walter/14.png");
        texturasCaminar[1] = new Texture("personaje/walter/15.png");
        texturasCaminar[2] = new Texture("personaje/walter/16.png");

        for (int i = 0; i < 3; i++)
        {
            framesCaminar[i] = new TextureRegion(texturasCaminar[i]);
        }


        animacionCaminar = new Animation<>(0.12f, framesCaminar);
        animacionCaminar.setPlayMode(Animation.PlayMode.LOOP);


        sprite = new Sprite(textura);

        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

        this.bounds = new Rectangle(x, y, ancho, alto);

        sprite.setPosition(x, y);
        sprite.setSize(ancho, alto);
    }


    public void dibujar()
    {
        sprite.draw(Render.batch);
    }


    public void update(Entrada entrada, float delta)
    {
        boolean caminando = false;

        // Gravedad
        if (!enSuelo)
        {
            velocidadY -= gravedad * delta;
        }

        y += velocidadY * delta;


        // Movimiento izquierda
        if (entrada.mueveIzquierda())
        {
            x -= velocidadX * delta;

            caminando = true;
            mirandoDerecha = false;
        }


        // Movimiento derecha
        if (entrada.mueveDerecha())
        {
            x += velocidadX * delta;

            caminando = true;
            mirandoDerecha = true;
        }


        // Salto
        if (entrada.mueveArriba())
        {
            saltar();
        }


        // Animación
        if (caminando)
        {
            tiempoAnimacion += delta;

            TextureRegion frameActual =
                animacionCaminar.getKeyFrame(tiempoAnimacion);

            sprite.setRegion(frameActual);

            // Dirección
            if (mirandoDerecha)
            {
                sprite.setFlip(false, false);
            }
            else
            {
                sprite.setFlip(true, false);
            }
        }
        else
        {
            // Walter quieto
            tiempoAnimacion = 0f;

            sprite.setRegion(textura);

            if (mirandoDerecha)
            {
                sprite.setFlip(false, false);
            }
            else
            {
                sprite.setFlip(true, false);
            }
        }


        // Actualizar posición
        sprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }


    private void saltar()
    {
        if (enSuelo)
        {
            velocidadY = 350;
            enSuelo = false;
        }
    }


    public void setEnSuelo(boolean valor)
    {
        this.enSuelo = valor;
    }


    public float getX()
    {
        return x;
    }


    public float getY()
    {
        return y;
    }


    public Rectangle getBounds()
    {
        return this.bounds;
    }


    public int getVelocidadX()
    {
        return velocidadX;
    }


    public int getVelocidadY()
    {
        return velocidadY;
    }


    public void setVelocidadX(int valor)
    {
        this.velocidadX = valor;
    }


    public void setVelocidadY(int valor)
    {
        this.velocidadY = valor;
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


    public float getAncho()
    {
        return this.ancho;
    }


    public float getAlto()
    {
        return this.alto;
    }
}
