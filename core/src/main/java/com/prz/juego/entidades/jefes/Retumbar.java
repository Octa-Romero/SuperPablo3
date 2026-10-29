
package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.enemigos.Enemigo;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;

public class Retumbar extends Enemigo {

    private static final float ANCHO = 300;
    private static final float ALTO = 400;
    private static final int VELOCIDAD = 20;
    private static final float RECORRIDO = 250;
    private static final double VIDA = 40;
    private static final double DANIO = 1;
    private static final Color COLOR_CUERPO = new Color(0.6f, 0f, 0f, 1f);

    private static final float TIEMPO_ENTRE_ATAQUES = 3f;
    private static final int REPETICIONES_LEVANTAR_BAJAR = 5;
    private static final int REPETICIONES_TELETRANSPORTE = 3;
    private static final float TIEMPO_ENTRE_TELETRANSPORTES = 1f;

    private static final float ALTURA_SUBIDA_LENTA = 100;
    private static final float DURACION_SUBIDA_LENTA = 2f;
    private static final float ALTURA_SUBIDA_RAPIDA = 200;
    private static final float VELOCIDAD_SUBIDA_RAPIDA = 2500;
    private static final float DURACION_ONDA_SUELO = 0.5f;
    private static final float ANCHO_SUELO_DEFECTO = 3000;

    private final float X_INICIAL;
    private final Mano MANO_IZQUIERDA;
    private final Mano MANO_DERECHA;
    private final OndaSuelo ONDA_SUELO;

    private enum TipoAtaque {
        LEVANTAR_BAJAR_MANOS,
        MANO_TELETRANSPORTE,
        ATAQUE_SUELO
    }

    private enum FaseSuelo {
        SUBIENDO_LENTO,
        SUBIDA_RAPIDA,
        VOLVIENDO
    }

    private TipoAtaque ataqueActual;
    private boolean atacando = false;
    private float tiempoAtaque = 0f;
    private int repeticionesAtaque = 0;
    private boolean manoDerechaAtacando = false;
    private Mano manoTeletransporte;
    private FaseSuelo faseSuelo;
    private float tiempoFase = 0f;
    private boolean esperandoTeletransporte = false;
    private float tiempoEsperaTeletransporte = 0f;

    public Retumbar(float centroX, float y, Jugador jugador) {
        this(
            centroX,
            y,
            jugador,
            centroX - ANCHO_SUELO_DEFECTO / 2f,
            centroX + ANCHO_SUELO_DEFECTO / 2f
        );
    }

    public Retumbar(
        float centroX,
        float y,
        Jugador jugador,
        float sueloXMin,
        float sueloXMax
    ) {
        super(
            centroX - ANCHO / 2f,
            y,
            ANCHO,
            ALTO,
            VELOCIDAD,
            VIDA,
            DANIO,
            jugador,
            GestorRecursos.obtenerPixel()
        );

        X_INICIAL = x;
        duracionInvencibilidad = 0f;
        sprite.setColor(COLOR_CUERPO);

        MANO_IZQUIERDA = new Mano(this, Mano.Lado.IZQUIERDA, jugador);
        MANO_DERECHA = new Mano(this, Mano.Lado.DERECHA, jugador);
        ONDA_SUELO = new OndaSuelo(sueloXMin, sueloXMax, y, jugador);
    }

    @Override
    public void update(float delta) {
        if (muerto) {
            return;
        }

        guardarPosicionAnterior();
        actualizarInvencibilidad(delta);
        actualizarComportamiento(delta, jugador.getBounds());

        bounds.setPosition(x, y);
        actualizarSpriteVisual();
    }

    @Override
    protected void actualizarComportamiento(float delta, Rectangle boundsJugador) {
        if (!atacando) {
            tiempoAtaque += delta;

            if (tiempoAtaque >= TIEMPO_ENTRE_ATAQUES) {
                iniciarAtaqueAleatorio();
            }
        }

        boolean quieto = atacando && ataqueActual == TipoAtaque.ATAQUE_SUELO;

        if (!quieto) {
            moverBoss(delta);
        }

        if (atacando) {
            ejecutarAtaque(delta);
        }
    }

    private void moverBoss(float delta) {
        x += direccion * velocidad * delta;

        if (x >= X_INICIAL + RECORRIDO) {
            x = X_INICIAL + RECORRIDO;
            direccion = -1;
        } else if (x <= X_INICIAL - RECORRIDO) {
            x = X_INICIAL - RECORRIDO;
            direccion = 1;
        }
    }

    private void iniciarAtaqueAleatorio() {
        boolean izquierdaViva = !MANO_IZQUIERDA.estaMuerto();
        boolean derechaViva = !MANO_DERECHA.estaMuerto();

        if (!izquierdaViva && !derechaViva) {
            tiempoAtaque = 0f;
            return;
        }

        TipoAtaque[] ataques = TipoAtaque.values();
        ataqueActual = ataques[(int) (Math.random() * ataques.length)];

        atacando = true;
        tiempoAtaque = 0f;
        repeticionesAtaque = 0;
        manoDerechaAtacando = true;
        esperandoTeletransporte = false;
        tiempoEsperaTeletransporte = 0f;
        manoTeletransporte = null;

        switch (ataqueActual) {
            case LEVANTAR_BAJAR_MANOS:
                MANO_DERECHA.iniciarLevantarBajar();
                break;

            case MANO_TELETRANSPORTE:
                manoTeletransporte = seleccionarManoTeletransporte(null);
                if (manoTeletransporte != null) {
                    manoTeletransporte.iniciarTeletransporte();
                } else {
                    terminarAtaque();
                }
                break;

            case ATAQUE_SUELO:
                iniciarAtaqueSuelo();
                break;
        }
    }

    private void ejecutarAtaque(float delta) {
        switch (ataqueActual) {
            case LEVANTAR_BAJAR_MANOS:
                ejecutarLevantarBajarManos();
                break;

            case MANO_TELETRANSPORTE:
                ejecutarManoTeletransporte(delta);
                break;

            case ATAQUE_SUELO:
                ejecutarAtaqueSuelo(delta);
                break;
        }
    }

    private void ejecutarLevantarBajarManos() {
        boolean manoDerechaLista = MANO_DERECHA.estaAtaqueCompletado();
        boolean manoIzquierdaLista = MANO_IZQUIERDA.estaAtaqueCompletado();

        if (manoDerechaAtacando && manoDerechaLista) {
            manoDerechaAtacando = false;
            MANO_IZQUIERDA.iniciarLevantarBajar();
        } else if (!manoDerechaAtacando && manoIzquierdaLista) {
            repeticionesAtaque++;

            if (repeticionesAtaque >= REPETICIONES_LEVANTAR_BAJAR) {
                terminarAtaque();
            } else {
                manoDerechaAtacando = true;
                MANO_DERECHA.iniciarLevantarBajar();
            }
        }
    }

    private void ejecutarManoTeletransporte(float delta) {
        if (esperandoTeletransporte) {
            tiempoEsperaTeletransporte -= delta;

            if (tiempoEsperaTeletransporte <= 0f) {
                manoTeletransporte = seleccionarManoTeletransporte(manoTeletransporte);

                if (manoTeletransporte == null) {
                    terminarAtaque();
                    return;
                }

                esperandoTeletransporte = false;
                manoTeletransporte.iniciarTeletransporte();
            }

            return;
        }

        if (manoTeletransporte == null || manoTeletransporte.estaMuerto()) {
            terminarAtaque();
            return;
        }

        if (manoTeletransporte.estaAtaqueCompletado()) {
            repeticionesAtaque++;

            if (repeticionesAtaque >= REPETICIONES_TELETRANSPORTE) {
                terminarAtaque();
            } else {
                esperandoTeletransporte = true;
                tiempoEsperaTeletransporte = TIEMPO_ENTRE_TELETRANSPORTES;
            }
        }
    }

    private Mano seleccionarManoTeletransporte(Mano manoAnterior) {
        boolean izquierdaViva = !MANO_IZQUIERDA.estaMuerto();
        boolean derechaViva = !MANO_DERECHA.estaMuerto();

        if (!izquierdaViva && !derechaViva) {
            return null;
        }

        if (izquierdaViva && !derechaViva) {
            return MANO_IZQUIERDA;
        }

        if (derechaViva && !izquierdaViva) {
            return MANO_DERECHA;
        }

        if (manoAnterior == MANO_IZQUIERDA) {
            return MANO_DERECHA;
        }

        if (manoAnterior == MANO_DERECHA) {
            return MANO_IZQUIERDA;
        }

        return Math.random() < 0.5 ? MANO_IZQUIERDA : MANO_DERECHA;
    }

    private void iniciarAtaqueSuelo() {
        faseSuelo = FaseSuelo.SUBIENDO_LENTO;
        tiempoFase = 0f;

        float velocidadLenta = ALTURA_SUBIDA_LENTA / DURACION_SUBIDA_LENTA;

        MANO_IZQUIERDA.iniciarSubida(ALTURA_SUBIDA_LENTA, velocidadLenta);
        MANO_DERECHA.iniciarSubida(ALTURA_SUBIDA_LENTA, velocidadLenta);
    }

    private void ejecutarAtaqueSuelo(float delta) {
        tiempoFase += delta;

        switch (faseSuelo) {
            case SUBIENDO_LENTO:
                if (tiempoFase >= DURACION_SUBIDA_LENTA) {
                    faseSuelo = FaseSuelo.SUBIDA_RAPIDA;
                    tiempoFase = 0f;

                    MANO_IZQUIERDA.iniciarSubida(
                        ALTURA_SUBIDA_RAPIDA,
                        VELOCIDAD_SUBIDA_RAPIDA
                    );
                    MANO_DERECHA.iniciarSubida(
                        ALTURA_SUBIDA_RAPIDA,
                        VELOCIDAD_SUBIDA_RAPIDA
                    );

                    ONDA_SUELO.activar(DURACION_ONDA_SUELO);
                }
                break;

            case SUBIDA_RAPIDA:
                if (tiempoFase >= DURACION_ONDA_SUELO) {
                    faseSuelo = FaseSuelo.VOLVIENDO;
                    MANO_IZQUIERDA.iniciarRegreso();
                    MANO_DERECHA.iniciarRegreso();
                }
                break;

            case VOLVIENDO:
                if (MANO_IZQUIERDA.estaAtaqueCompletado()
                    && MANO_DERECHA.estaAtaqueCompletado()) {
                    terminarAtaque();
                }
                break;
        }
    }

    private void terminarAtaque() {
        atacando = false;
        tiempoAtaque = 0f;
        esperandoTeletransporte = false;
        tiempoEsperaTeletransporte = 0f;
        manoTeletransporte = null;
    }

    @Override
    public boolean puedeRecibirDanio() {
        return MANO_IZQUIERDA.estaMuerto() && MANO_DERECHA.estaMuerto();
    }

    @Override
    public void restarVida(double cantidad) {
        if (puedeRecibirDanio()) {
            super.restarVida(cantidad);
        }
    }

    @Override
    public void saltar() {}

    public Mano getManoIzquierda() {
        return MANO_IZQUIERDA;
    }

    public Mano getManoDerecha() {
        return MANO_DERECHA;
    }

    public OndaSuelo getOndaSuelo() {
        return ONDA_SUELO;
    }
}
