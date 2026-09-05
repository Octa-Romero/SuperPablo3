package com.prz.juego.entidades;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.utilidades.Render;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.prz.juego.utilidades.Debug;

public class OrbeMagica {

    private static final float VELOCIDAD = 400f;
    private static final float DISTANCIA_MAXIMA = 400f;
    private static final float RADIO = 12f;
    private float x;
    private float y;
    private final float direccion;
    private float distanciaRecorrida;
    private final Sprite sprite;
    private final Circle bounds;
    private boolean activa = true;
    private final Walter walter;
    private final Colisiones colision;
    private final ArrayList<Entidad> entidades;

    public OrbeMagica(float x, float y, float direccion, Walter walter, Colisiones colision, ArrayList<Entidad> entidades) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.walter = walter;
        this.colision = colision;
        this.entidades = entidades;
        sprite = new Sprite(
            new Texture("Personajes/Walter/orbe.png")
        );
        sprite.setSize(RADIO * 2, RADIO * 2);
        sprite.setPosition(x - RADIO, y - RADIO);
        bounds = new Circle(x, y, RADIO);
    }

    public void update(float delta) {
        if (!activa) {
            return;
        }

        float movimiento = VELOCIDAD * delta;

        x += direccion * movimiento;
        distanciaRecorrida += movimiento;

        bounds.setPosition(x, y);
        sprite.setPosition(x - RADIO, y - RADIO);

        if (distanciaRecorrida >= DISTANCIA_MAXIMA) {
            activa = false;
            return;
        }

        if (chocaConPared()) {
            activa = false;
            return;
        }

        for (Entidad entidad : entidades) {
            if (!(entidad instanceof Enemigo)) {
                continue;
            }

            Enemigo enemigo = (Enemigo) entidad;

            if (enemigo.estaMuerto()) {
                continue;
            }

            if (chocaConEnemigo(enemigo)) {
                enemigo.restarVida(walter.getDanio());
                activa = false;
                return;
            }
        }
    }

    private boolean chocaConPared() {
        for (Rectangle pared : colision.getColisiones()) {
            float puntoX = Math.max(pared.x, Math.min(x, pared.x + pared.width));
            float puntoY = Math.max(pared.y, Math.min(y, pared.y + pared.height));
            float distanciaX = x - puntoX;
            float distanciaY = y - puntoY;

            if (distanciaX * distanciaX + distanciaY * distanciaY <= RADIO * RADIO) {
                return true;
            }
        }
        return false;
    }

    private boolean chocaConEnemigo(Enemigo enemigo) {
        Rectangle enemigoBounds = enemigo.getBounds();
        float puntoX = Math.max(enemigoBounds.x, Math.min(x, enemigoBounds.x + enemigoBounds.width));
        float puntoY = Math.max(enemigoBounds.y, Math.min(y, enemigoBounds.y + enemigoBounds.height));
        float distanciaX = x - puntoX;
        float distanciaY = y - puntoY;

        return distanciaX * distanciaX + distanciaY * distanciaY <= RADIO * RADIO;
    }

    public void dibujar() {
        if (activa) {
            sprite.draw(Render.batch);
        }
    }

    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (!activa) {
            return;
        }

        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(bounds.x, bounds.y, bounds.radius);
    }

    public boolean estaActiva() {
        return activa;
    }

    public void dispose() {
        if (sprite != null && sprite.getTexture() != null) {
            sprite.getTexture().dispose();
        }
    }
}
