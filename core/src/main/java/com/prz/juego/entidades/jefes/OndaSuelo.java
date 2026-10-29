
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class OndaSuelo extends Enemigo {

    private static final float ALTO = 50f;
    private static final double DANIO = 1;
    private static final float TIEMPO_EMERGER = 0.1f;
    private static final float TIEMPO_BAJAR = 0.1f;
    private static final float ALTO_MINIMO = 1f;
    private static final float X_OCULTA = -100000f;

    private final float X_INICIO;
    private final float Y_SUELO;

    private boolean activa = false;
    private boolean bajando = false;
    private float tiempoActiva = 0f;
    private float tiempoRestante = 0f;
    private float tiempoBajada = 0f;

    public OndaSuelo(float xMin, float xMax, float ySuelo, Jugador jugador) {
        super(
            xMin,
            ySuelo,
            xMax - xMin,
            ALTO,
            0,
            1,
            DANIO,
            jugador,
            GestorRecursos.obtenerPixel()
        );

        X_INICIO = xMin;
        Y_SUELO = ySuelo;
        duracionInvencibilidad = 0f;
        sprite.setColor(Color.RED);
        ocultar();
    }

    @Override
    public void update(float delta) {
        if (!activa) {
            sprite.setAlpha(0f);
            return;
        }

        actualizarComportamiento(delta, jugador.getBounds());
        sprite.setAlpha(activa ? 1f : 0f);
    }

    @Override
    protected void actualizarComportamiento(float delta, Rectangle boundsJugador) {
        if (bajando) {
            tiempoBajada += delta;

            float progreso = Math.max(
                0f,
                1f - tiempoBajada / TIEMPO_BAJAR
            );

            aplicarAlto(Math.max(ALTO_MINIMO, ALTO * progreso));

            if (tiempoBajada >= TIEMPO_BAJAR) {
                ocultar();
            }

            return;
        }

        tiempoActiva += delta;
        tiempoRestante -= delta;

        float progreso = Math.min(1f, tiempoActiva / TIEMPO_EMERGER);
        aplicarAlto(Math.max(ALTO_MINIMO, ALTO * progreso));

        if (bounds.overlaps(boundsJugador)) {
            jugador.restarVida(danio);
        }

        if (tiempoRestante <= 0f) {
            bajando = true;
            tiempoBajada = 0f;
        }
    }

    public void activar(float duracion) {
        activa = true;
        bajando = false;
        tiempoActiva = 0f;
        tiempoRestante = duracion;
        tiempoBajada = 0f;

        x = X_INICIO;
        y = Y_SUELO;
        aplicarAlto(ALTO_MINIMO);
        sprite.setAlpha(1f);
    }

    private void ocultar() {
        activa = false;
        bajando = false;
        x = X_OCULTA;
        y = Y_SUELO;
        aplicarAlto(ALTO);
        sprite.setAlpha(0f);
    }

    private void aplicarAlto(float nuevoAlto) {
        alto = nuevoAlto;
        altoSprite = nuevoAlto;
        bounds.set(x, y, ancho, nuevoAlto);
        actualizarSpriteVisual();
    }

    public boolean estaActiva() {
        return activa;
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (activa) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height
            );
        }
    }

    @Override
    public boolean puedeRecibirDanio() {
        return false;
    }

    @Override
    public void restarVida(double cantidad) {}

    @Override
    public void saltar() {}
}
