package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

final class Meteorito {

    private static final float TAMANO_METEORITO = 82f;
    private static final float ANCHO_FUEGO = 110f;
    private static final float ALTO_FUEGO = 38f;
    private static final float VELOCIDAD_ROTACION = 180f;
    private static final float DURACION_FUEGO = 15f;
    private static final float DANIO = 1f;
    private static final float GRAVEDAD = 250f;

    private final float x;
    private final float sueloY;
    private final Jugador jugador;
    private final Animation<TextureRegion> animacionFuego;
    private final Sprite sprite;
    private final Rectangle bounds = new Rectangle();
    private final int sentidoRotacion = Math.random() < 0.5 ? 1 : -1;
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
        this.x = x;
        this.y = y;
        this.sueloY = sueloY;
        this.jugador = jugador;
        this.animacionFuego = animacionFuego;
        sprite = new Sprite(textura);
        sprite.setSize(TAMANO_METEORITO, TAMANO_METEORITO);
        sprite.setOriginCenter();
        sprite.setPosition(x - TAMANO_METEORITO / 2f, y);
        bounds.set(x - TAMANO_METEORITO / 2f, y, TAMANO_METEORITO, TAMANO_METEORITO);
    }

    boolean update(float delta) {
        if (!impacto) {
            velocidadY -= GRAVEDAD * delta;
            y += velocidadY * delta;
            sprite.setPosition(x - TAMANO_METEORITO / 2f, y);
            sprite.rotate(sentidoRotacion * VELOCIDAD_ROTACION * delta);
            bounds.setPosition(x - TAMANO_METEORITO / 2f, y);

            if (y <= sueloY) {
                impacto = true;
                tiempoFuego = 0f;
                y = sueloY;
                bounds.set(x - ANCHO_FUEGO / 2f, sueloY, ANCHO_FUEGO, ALTO_FUEGO);
                Sonido.METEORITO.sonar(x, jugador.getX() + jugador.getAncho() / 2f);
            } else if (bounds.overlaps(jugador.getBounds())) {
                jugador.restarVida(DANIO);
            }
            return true;
        }

        tiempoFuego += delta;
        sprite.setRegion(animacionFuego.getKeyFrame(tiempoFuego));
        sprite.setSize(ANCHO_FUEGO, ALTO_FUEGO);
        sprite.setRotation(0f);
        sprite.setPosition(x - ANCHO_FUEGO / 2f, sueloY);

        if (bounds.overlaps(jugador.getBounds())) {
            jugador.restarVida(DANIO);
        }
        return tiempoFuego < DURACION_FUEGO;
    }

    Rectangle getBounds() {
        return bounds;
    }

    void dibujar() {
        sprite.draw(Render.batch);
    }
}
