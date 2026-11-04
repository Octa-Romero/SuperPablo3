package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

class Meteorito {

    private static final float TAMANO_METEORITO = 82f;
    private static final float ANCHO_FUEGO = 110f;
    private static final float ALTO_FUEGO = 38f;
    private static final float VELOCIDAD_ROTACION = 180f;
    private static final float DURACION_FUEGO = 15f;
    private static final float DANIO = 1f;
    private static final float GRAVEDAD = 250f;

    private final float X;
    private final float SUELO_Y;
    private final Jugador JUGADOR;
    private final Animation<TextureRegion> ANIMACION_FUEGO;
    private final Sprite SPRITE;
    private final Rectangle BOUNDS = new Rectangle();
    private final int SENTIDO_ROTACION = Math.random() < 0.5 ? 1 : -1;
    private float y;
    private float velocidadY;
    private float tiempoFuego;
    private boolean impacto;

    Meteorito(
            float x,
            float y,
            float sueloY,
            Jugador jugador,
            Texture textura,
            Animation<TextureRegion> animacionFuego) {
        this.X = x;
        this.y = y;
        this.SUELO_Y = sueloY;
        this.JUGADOR = jugador;
        this.ANIMACION_FUEGO = animacionFuego;
        SPRITE = new Sprite(textura);
        SPRITE.setSize(TAMANO_METEORITO, TAMANO_METEORITO);
        SPRITE.setOriginCenter();
        SPRITE.setPosition(x - TAMANO_METEORITO / 2f, y);
        BOUNDS.set(x - TAMANO_METEORITO / 2f, y, TAMANO_METEORITO, TAMANO_METEORITO);
    }

    boolean update(float delta) {
        if (!impacto) {
            velocidadY -= GRAVEDAD * delta;
            y += velocidadY * delta;
            SPRITE.setPosition(X - TAMANO_METEORITO / 2f, y);
            SPRITE.rotate(SENTIDO_ROTACION * VELOCIDAD_ROTACION * delta);
            BOUNDS.setPosition(X - TAMANO_METEORITO / 2f, y);

            if (y <= SUELO_Y) {
                impacto = true;
                tiempoFuego = 0f;
                y = SUELO_Y;
                BOUNDS.set(X - ANCHO_FUEGO / 2f, SUELO_Y, ANCHO_FUEGO, ALTO_FUEGO);
                Sonido.METEORITO.sonar();
            } else if (BOUNDS.overlaps(JUGADOR.getBounds())) {
                JUGADOR.restarVida(DANIO);
            }
            return true;
        }

        tiempoFuego += delta;
        SPRITE.setRegion(ANIMACION_FUEGO.getKeyFrame(tiempoFuego));
        SPRITE.setSize(ANCHO_FUEGO, ALTO_FUEGO);
        SPRITE.setRotation(0f);
        SPRITE.setPosition(X - ANCHO_FUEGO / 2f, SUELO_Y);

        if (BOUNDS.overlaps(JUGADOR.getBounds())) {
            JUGADOR.restarVida(DANIO);
        }
        return tiempoFuego < DURACION_FUEGO;
    }

    Rectangle getBounds() {
        return BOUNDS;
    }

    void dibujar() {
        SPRITE.draw(Render.batch);
    }
}
