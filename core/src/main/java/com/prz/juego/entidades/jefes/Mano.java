package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class Mano extends Enemigo {

    public enum Lado {
        IZQUIERDA,
        DERECHA
    }

    private static final float ANCHO = 120;
    private static final float ALTO = 100;
    private static final float SEPARACION = 30;
    private static final double VIDA = 20;
    private static final double DANIO = 1;

    private static final float ALTURA_LEVANTADA = 300;
    private static final float VELOCIDAD_LEVANTAR = 500;
    private static final float VELOCIDAD_BAJAR = 1800;
    private static final float VELOCIDAD_VOLVER = 600;
    private static final float TIEMPO_DESPUES_DESAPARECER = 0.5f;
    private static final float ALTURA_SOBRE_JUGADOR = 400;

    private static final float TIEMPO_SEGUIR_JUGADOR = 1f;
    private static final float TIEMPO_ESPERA_GOLPE = 0.8f;
    private static final float TIEMPO_RECUPERACION = 0.4f;
    private static final float DURACION_HITBOX_EXTENDIDA = 0.2f;
    private static final float EXTENSION_HITBOX = 100;

    private enum EstadoAtaque {
        NINGUNO,
        LEVANTANDO,
        BAJANDO,
        RECUPERANDO,
        DESAPARECIENDO,
        ESPERANDO_REAPARICION,
        SIGUIENDO_JUGADOR,
        ESPERANDO_GOLPE,
        MOVIENDO,
        EN_POSICION,
        VOLVIENDO
    }

    private final Retumbar RETUMBAR;
    private final Lado LADO;

    private EstadoAtaque estadoAtaque = EstadoAtaque.NINGUNO;
    private boolean haciendoDanio = false;
    private boolean teletransportada = false;
    private float tiempoHitboxExtendida = 0f;
    private float tiempoEstado = 0f;
    private boolean visible = true;
    private float yAtaque;
    private float yObjetivo;
    private float yDestino;
    private float velocidadMovimiento;

    public Mano(Retumbar RETUMBAR, Lado LADO, Jugador jugador) {
        super(
            0,
            RETUMBAR.getY(),
            ANCHO,
            ALTO,
            0,
            VIDA,
            DANIO,
            jugador,
            GestorRecursos.obtenerPixel());
        this.RETUMBAR = RETUMBAR;
        this.LADO = LADO;
        duracionInvencibilidad = 0f;
        sprite.setColor(Color.RED);
        seguirJefe();
    }

    @Override
    public void update(float delta) {
        if (muerto) {
            return;
        }

        guardarPosicionAnterior();
        actualizarInvencibilidad(delta);
        actualizarComportamiento(delta, jugador.getBounds());

        if (tiempoHitboxExtendida > 0) {
            tiempoHitboxExtendida -= delta;
            actualizarHitboxExtendida();
        }

        if (haciendoDanio || tiempoHitboxExtendida > 0) {
            verificarColisionJugador();
        }

        sprite.setAlpha(visible ? 1f : 0f);
    }

    @Override
    protected void actualizarComportamiento(float delta, Rectangle boundsJugador) {
        if (estadoAtaque == EstadoAtaque.NINGUNO) {
            seguirJefe();
        } else {
            ejecutarAtaque(delta);
        }
    }

    private void ejecutarAtaque(float delta) {
        switch (estadoAtaque) {
            case LEVANTANDO:
                y += VELOCIDAD_LEVANTAR * delta;
                if (y >= yAtaque) {
                    y = yAtaque;
                    estadoAtaque = EstadoAtaque.BAJANDO;
                }
                break;
            case BAJANDO:
                y -= VELOCIDAD_BAJAR * delta;
                haciendoDanio = true;
                if (y <= yObjetivo) {
                    y = yObjetivo;
                    haciendoDanio = false;
                    tiempoHitboxExtendida = DURACION_HITBOX_EXTENDIDA;
                    estadoAtaque = EstadoAtaque.RECUPERANDO;
                    tiempoEstado = TIEMPO_RECUPERACION;
                }
                break;
            case RECUPERANDO:
                tiempoEstado -= delta;
                if (tiempoEstado <= 0) {
                    estadoAtaque = EstadoAtaque.NINGUNO;
                }
                break;
            case DESAPARECIENDO:
                visible = false;
                estadoAtaque = EstadoAtaque.ESPERANDO_REAPARICION;
                tiempoEstado = TIEMPO_DESPUES_DESAPARECER;
                break;
            case ESPERANDO_REAPARICION:
                tiempoEstado -= delta;
                if (tiempoEstado <= 0) {
                    visible = true;
                    x = jugador.getX() + jugador.getAncho() / 2 - ANCHO / 2;
                    y = jugador.getY() + ALTURA_SOBRE_JUGADOR;
                    estadoAtaque = EstadoAtaque.SIGUIENDO_JUGADOR;
                    tiempoEstado = TIEMPO_SEGUIR_JUGADOR;
                }
                break;
            case SIGUIENDO_JUGADOR:
                tiempoEstado -= delta;
                x = jugador.getX() + jugador.getAncho() / 2 - ANCHO / 2;
                if (tiempoEstado <= 0) {
                    estadoAtaque = EstadoAtaque.ESPERANDO_GOLPE;
                    tiempoEstado = TIEMPO_ESPERA_GOLPE;
                }
                break;
            case ESPERANDO_GOLPE:
                tiempoEstado -= delta;
                if (tiempoEstado <= 0) {
                    yObjetivo = RETUMBAR.getY();
                    estadoAtaque = EstadoAtaque.BAJANDO;
                }
                break;
            case MOVIENDO:
                velocidadY = 1f;
                y += velocidadMovimiento * delta;
                if (y >= yDestino) {
                    y = yDestino;
                    velocidadY = 0f;
                    estadoAtaque = EstadoAtaque.EN_POSICION;
                }
                break;
            case EN_POSICION:
                // Quieta en el aire hasta que Retumbar ordene otra cosa
                break;
            case VOLVIENDO:
                y -= VELOCIDAD_VOLVER * delta;
                if (y <= RETUMBAR.getY()) {
                    y = RETUMBAR.getY();
                    estadoAtaque = EstadoAtaque.NINGUNO;
                }
                break;
            default:
                break;
        }

        if (debeSeguirEnX()) {
            seguirJefeEnX();
        }
        bounds.setPosition(x, y);
        actualizarSpriteVisual();
    }

    private boolean debeSeguirEnX() {
        switch (estadoAtaque) {
            case DESAPARECIENDO:
            case ESPERANDO_REAPARICION:
            case SIGUIENDO_JUGADOR:
            case ESPERANDO_GOLPE:
                return false;
            case BAJANDO:
            case RECUPERANDO:
                return !teletransportada;
            default:
                return true;
        }
    }

    private void seguirJefeEnX() {
        if (LADO == Lado.IZQUIERDA) {
            x = RETUMBAR.getX() - SEPARACION - ancho;
        } else {
            x = RETUMBAR.getX() + RETUMBAR.getAncho() + SEPARACION;
        }
    }

    private void seguirJefe() {
        seguirJefeEnX();
        y = RETUMBAR.getY();

        bounds.setPosition(x, y);
        actualizarSpriteVisual();
    }

    private void verificarColisionJugador() {
        if (bounds.overlaps(jugador.getBounds())) {
            jugador.restarVida(danio);
        }
    }

    private void actualizarHitboxExtendida() {
        if (tiempoHitboxExtendida > 0) {
            bounds.setSize(ANCHO + EXTENSION_HITBOX, bounds.height);
            bounds.setPosition(x - EXTENSION_HITBOX / 2, y);
        } else {
            bounds.setSize(ANCHO, bounds.height);
            bounds.setPosition(x, y);
        }
        actualizarSpriteVisual();
    }

    @Override
    public void dibujarHitbox(ShapeRenderer shapeRenderer) {
        if (tiempoHitboxExtendida > 0) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(x - EXTENSION_HITBOX / 2, y, ANCHO + EXTENSION_HITBOX, ALTO);
        } else {
            super.dibujarHitbox(shapeRenderer);
        }
    }

    @Override
    public void restarVida(double cantidad) {
        if (!visible) {
            return;
        }
        super.restarVida(cantidad);
    }

    public void iniciarLevantarBajar() {
        if (muerto) {
            return;
        }
        teletransportada = false;
        estadoAtaque = EstadoAtaque.LEVANTANDO;
        yAtaque = RETUMBAR.getY() + ALTURA_LEVANTADA;
        yObjetivo = RETUMBAR.getY();
    }

    public void iniciarTeletransporte() {
        if (muerto) {
            return;
        }
        teletransportada = true;
        estadoAtaque = EstadoAtaque.DESAPARECIENDO;
    }

    public void iniciarSubida(float alturaSobreSuelo, float velocidad) {
        if (muerto) {
            return;
        }
        teletransportada = false;
        estadoAtaque = EstadoAtaque.MOVIENDO;
        yDestino = RETUMBAR.getY() + alturaSobreSuelo;
        velocidadMovimiento = velocidad;
    }

    public void iniciarRegreso() {
        if (muerto) {
            return;
        }
        velocidadY = 0f;
        estadoAtaque = EstadoAtaque.VOLVIENDO;
    }

    public boolean estaAtaqueCompletado() {
        // Una mano muerta ya no puede atacar: se considera completado para no trabar al jefe
        return muerto || estadoAtaque == EstadoAtaque.NINGUNO;
    }

    public boolean estaEnAtaque() {
        return !muerto && estadoAtaque != EstadoAtaque.NINGUNO;
    }

    @Override
    public void saltar() {}

    public Lado getLado() {
        return LADO;
    }
}
