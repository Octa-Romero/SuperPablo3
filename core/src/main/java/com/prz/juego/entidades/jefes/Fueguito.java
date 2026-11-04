package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;

class Fueguito {

    private static final float DANIO = 1f;
    private static final float ESCALA_ALTO_SPRITE = 1.5f;
    private static final int CANTIDAD_FRAMES = 2;
    private static final float DURACION_FRAME = 0.08f;

    private final Sprite SPRITE;
    private final Animation<TextureRegion> ANIMACION;
    private final Circle HITBOX = new Circle();
    private final float TAMANO;
    private final float VELOCIDAD;
    private final float SUELO_Y;
    private final Jugador JUGADOR;
    private float y;
    private float tiempoAnimacion;

    Fueguito(
            float x,
            float y,
            float tamano,
            float velocidad,
            float sueloY,
            Jugador jugador,
            Texture textura) {
        this.y = y;
        this.TAMANO = tamano;
        this.VELOCIDAD = velocidad;
        this.SUELO_Y = sueloY;
        this.JUGADOR = jugador;
        TextureRegion[][] frames = TextureRegion.split(
            textura,
            textura.getWidth() / CANTIDAD_FRAMES,
            textura.getHeight());
        ANIMACION = new Animation<>(DURACION_FRAME, frames[0][0], frames[0][1]);
        ANIMACION.setPlayMode(Animation.PlayMode.LOOP);
        SPRITE = new Sprite(frames[0][0]);
        SPRITE.setSize(tamano, tamano * ESCALA_ALTO_SPRITE);
        SPRITE.setPosition(x - tamano / 2f, y);
        HITBOX.set(x, y + tamano / 2f, tamano / 2f);
    }

    boolean update(float delta) {
        tiempoAnimacion += delta;
        SPRITE.setRegion(ANIMACION.getKeyFrame(tiempoAnimacion));
        y -= VELOCIDAD * delta;
        SPRITE.setY(y);
        HITBOX.setY(y + TAMANO / 2f);

        if (y <= SUELO_Y) {
            return false;
        }

        if (Intersector.overlaps(HITBOX, JUGADOR.getBounds())) {
            JUGADOR.restarVida(DANIO);
            return false;
        }
        return true;
    }

    Circle getHitbox() {
        return HITBOX;
    }

    void dibujar() {
        SPRITE.draw(Render.batch);
    }
}
