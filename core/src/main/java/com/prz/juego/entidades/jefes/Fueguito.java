package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Render;

final class Fueguito {

    private static final float DANIO = 1f;
    private static final float ESCALA_ALTO_SPRITE = 1.5f;

    private final Sprite sprite;
    private final Circle hitbox = new Circle();
    private final float tamano;
    private final float velocidad;
    private final float sueloY;
    private final Jugador jugador;
    private float y;

    Fueguito(
            float x,
            float y,
            float tamano,
            float velocidad,
            float sueloY,
            Jugador jugador,
            Texture textura) {
        this.y = y;
        this.tamano = tamano;
        this.velocidad = velocidad;
        this.sueloY = sueloY;
        this.jugador = jugador;
        sprite = new Sprite(textura);
        sprite.setSize(tamano, tamano * ESCALA_ALTO_SPRITE);
        sprite.setPosition(x - tamano / 2f, y);
        hitbox.set(x, y + tamano / 2f, tamano / 2f);
    }

    boolean update(float delta) {
        y -= velocidad * delta;
        sprite.setY(y);
        hitbox.setY(y + tamano / 2f);

        if (Intersector.overlaps(hitbox, jugador.getBounds())) {
            jugador.restarVida(DANIO);
            return false;
        }
        return y + tamano > sueloY;
    }

    Circle getHitbox() {
        return hitbox;
    }

    void dibujar() {
        sprite.draw(Render.batch);
    }
}
