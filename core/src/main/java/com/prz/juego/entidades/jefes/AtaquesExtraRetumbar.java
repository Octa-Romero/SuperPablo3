package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.enemigos.rango.BichoRango;
import com.prz.juego.entidades.enemigos.rango.EnemigoRango;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

final class AtaquesExtraRetumbar {

    private static final float COOLDOWN = 10f;
    private static final float INTERVALO_METEORITOS = 0.5f;
    private static final float DURACION_FUEGO = 15f;
    private static final float DURACION_AVISO_RAYO = 1.5f;
    private static final float INTERVALO_PARPADEO_RAYO = 0.15f;
    private static final float DURACION_RAYO = 0.3f;
    private static final float PAUSA_ENTRE_OLEADAS = 0.6f;
    private static final int CANTIDAD_METEORITOS = 3;
    private static final int CANTIDAD_RAYOS = 5;
    private static final int CANTIDAD_OLEADAS = 3;
    private static final float ANCHO_RAYO = 95f;
    private static final float DANIO = 1;
    private static final float GRAVEDAD_METEORITO = 250f;

    private enum TipoAtaque {
        NINGUNO,
        METEORITOS,
        BICHOS,
        RAYOS
    }

    private enum FaseRayos {
        AVISO,
        IMPACTO,
        PAUSA
    }

    private final Jugador jugador;
    private final float anchoMapa;
    private final float altoMapa;
    private final float sueloY;
    private final Consumer<Entidad> agregarEntidad;
    private final Random random = new Random();
    private final List<Meteorito> meteoritos = new ArrayList<>();
    private final Texture texturaMeteorito;
    private final Texture texturaRayo;
    private final Texture texturaPixel;
    private final Animation<TextureRegion> animacionFuego;

    private TipoAtaque ataqueActual = TipoAtaque.NINGUNO;
    private float tiempoCooldown;
    private float tiempoEntreMeteoritos;
    private int meteoritosInvocados;
    private float tiempoFaseRayos;
    private float tiempoParpadeoRayo;
    private int oleadaRayo;
    private FaseRayos faseRayos;
    private boolean mostrarAvisoRayo;
    private final float[] posicionesRayos = new float[CANTIDAD_RAYOS];

    AtaquesExtraRetumbar(
            Jugador jugador,
            float anchoMapa,
            float altoMapa,
            float sueloY,
            Consumer<Entidad> agregarEntidad) {
        this.jugador = jugador;
        this.anchoMapa = anchoMapa;
        this.altoMapa = altoMapa;
        this.sueloY = sueloY;
        this.agregarEntidad = agregarEntidad;

        texturaMeteorito = GestorRecursos.obtenerTextura("Jefe/meteorito.png");
        Texture texturaFuego = GestorRecursos.obtenerTextura("Jefe/fuegoMeteorito.png");
        texturaRayo = GestorRecursos.obtenerTextura("Jefe/rayo.png");
        texturaPixel = GestorRecursos.obtenerPixel();

        TextureRegion[][] frames = TextureRegion.split(texturaFuego, 1024, 342);
        animacionFuego =
                new Animation<>(0.14f, frames[0][0], frames[0][1], frames[0][2]);
        animacionFuego.setPlayMode(Animation.PlayMode.LOOP);
    }

    void update(float delta) {
        actualizarMeteoritos(delta);

        if (ataqueActual == TipoAtaque.NINGUNO) {
            tiempoCooldown += delta;
            if (tiempoCooldown >= COOLDOWN) {
                iniciarAtaqueAleatorio();
            }
        }

        switch (ataqueActual) {
            case METEORITOS:
                actualizarAtaqueMeteoritos(delta);
                break;
            case RAYOS:
                actualizarAtaqueRayos(delta);
                break;
            case BICHOS:
            case NINGUNO:
                break;
        }
    }

    private void iniciarAtaqueAleatorio() {
        TipoAtaque[] ataques = {
            TipoAtaque.METEORITOS,
            TipoAtaque.BICHOS,
            TipoAtaque.RAYOS
        };
        ataqueActual = ataques[random.nextInt(ataques.length)];
        tiempoCooldown = 0f;

        switch (ataqueActual) {
            case METEORITOS:
                meteoritosInvocados = 0;
                tiempoEntreMeteoritos = 0f;
                break;
            case BICHOS:
                invocarBichos();
                terminarAtaque();
                break;
            case RAYOS:
                oleadaRayo = 0;
                iniciarOleadaRayos();
                break;
        }
    }

    private void actualizarAtaqueMeteoritos(float delta) {
        tiempoEntreMeteoritos -= delta;
        if (tiempoEntreMeteoritos > 0f) {
            return;
        }

        invocarMeteorito();
        meteoritosInvocados++;

        if (meteoritosInvocados == CANTIDAD_METEORITOS) {
            terminarAtaque();
        } else {
            tiempoEntreMeteoritos = INTERVALO_METEORITOS;
        }
    }

    private void invocarMeteorito() {
        float margen = 45f;
        float x = margen + random.nextFloat() * Math.max(0f, anchoMapa - 2f * margen);
        meteoritos.add(new Meteorito(x, altoMapa + 30f));
    }

    private void invocarBichos() {
        float anchoBicho = 70f;
        BichoRango izquierdo =
                new BichoRango(50f, altoMapa + 30f, jugador, EnemigoRango.Modo.MOVIL);
        BichoRango derecho =
                new BichoRango(
                        Math.max(50f, anchoMapa - 50f - anchoBicho),
                        altoMapa + 30f,
                        jugador,
                        EnemigoRango.Modo.MOVIL);
        izquierdo.configurarRetrasoInicialAntesDeAtacar(1f);
        derecho.configurarRetrasoInicialAntesDeAtacar(1f);
        derecho.configurarDireccionInicial(-1f);
        agregarEntidad.accept(izquierdo);
        agregarEntidad.accept(derecho);
    }

    private void iniciarOleadaRayos() {
        for (int i = 0; i < posicionesRayos.length; i++) {
            float margen = ANCHO_RAYO / 2f;
            posicionesRayos[i] =
                    margen + random.nextFloat() * Math.max(0f, anchoMapa - 2f * margen);
        }

        faseRayos = FaseRayos.AVISO;
        tiempoFaseRayos = 0f;
        tiempoParpadeoRayo = 0f;
        mostrarAvisoRayo = true;
    }

    private void actualizarAtaqueRayos(float delta) {
        tiempoFaseRayos += delta;

        if (faseRayos == FaseRayos.AVISO) {
            tiempoParpadeoRayo += delta;
            if (tiempoParpadeoRayo >= INTERVALO_PARPADEO_RAYO) {
                tiempoParpadeoRayo %= INTERVALO_PARPADEO_RAYO;
                mostrarAvisoRayo = !mostrarAvisoRayo;
            }
        }

        if (faseRayos == FaseRayos.AVISO && tiempoFaseRayos >= DURACION_AVISO_RAYO) {
            faseRayos = FaseRayos.IMPACTO;
            tiempoFaseRayos = 0f;
            daniarJugadorPorRayos();
        } else if (faseRayos == FaseRayos.IMPACTO
                && tiempoFaseRayos >= DURACION_RAYO) {
            oleadaRayo++;
            if (oleadaRayo >= CANTIDAD_OLEADAS) {
                terminarAtaque();
            } else {
                faseRayos = FaseRayos.PAUSA;
                tiempoFaseRayos = 0f;
            }
        } else if (faseRayos == FaseRayos.PAUSA
                && tiempoFaseRayos >= PAUSA_ENTRE_OLEADAS) {
            iniciarOleadaRayos();
        }
    }

    private void daniarJugadorPorRayos() {
        Rectangle boundsJugador = jugador.getBounds();
        float alturaRayo = altoMapa - sueloY;
        float centroJugador = boundsJugador.x + boundsJugador.width / 2f;
        float rayoMasCercano = posicionesRayos[0];
        for (float x : posicionesRayos) {
            Rectangle boundsRayo =
                    new Rectangle(x - ANCHO_RAYO / 2f, sueloY, ANCHO_RAYO, alturaRayo);
            if (boundsJugador.overlaps(boundsRayo)) {
                jugador.restarVida(DANIO);
            }
            if (Math.abs(x - centroJugador) < Math.abs(rayoMasCercano - centroJugador)) {
                rayoMasCercano = x;
            }
        }
        Sonido.RELAMPAGO.sonar(rayoMasCercano, centroJugador);
    }

    private void terminarAtaque() {
        ataqueActual = TipoAtaque.NINGUNO;
        tiempoCooldown = 0f;
    }

    private void actualizarMeteoritos(float delta) {
        meteoritos.removeIf(meteorito -> !meteorito.update(delta));
    }

    void dibujar() {
        for (Meteorito meteorito : meteoritos) {
            meteorito.dibujar();
        }

        if (ataqueActual != TipoAtaque.RAYOS) {
            return;
        }

        if (faseRayos == FaseRayos.AVISO) {
            dibujarAvisosRayos();
        } else if (faseRayos == FaseRayos.IMPACTO) {
            dibujarRayos();
        }
    }

    void dibujarHitboxes(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.MAGENTA);
        for (Meteorito meteorito : meteoritos) {
            shapeRenderer.rect(
                    meteorito.bounds.x,
                    meteorito.bounds.y,
                    meteorito.bounds.width,
                    meteorito.bounds.height);
        }

        if (ataqueActual == TipoAtaque.RAYOS && faseRayos == FaseRayos.IMPACTO) {
            float alturaRayo = altoMapa - sueloY;
            for (float x : posicionesRayos) {
                shapeRenderer.rect(
                        x - ANCHO_RAYO / 2f, sueloY, ANCHO_RAYO, alturaRayo);
            }
        }
    }

    private void dibujarAvisosRayos() {
        if (!mostrarAvisoRayo) {
            return;
        }

        Color colorAnterior = Render.batch.getColor().cpy();
        Render.batch.setColor(1f, 0f, 0f, 0.35f);
        for (float x : posicionesRayos) {
            Render.batch.draw(
                    texturaPixel,
                    x - ANCHO_RAYO / 2f,
                    sueloY,
                    ANCHO_RAYO,
                    altoMapa - sueloY);
        }
        Render.batch.setColor(colorAnterior);
    }

    private void dibujarRayos() {
        for (float x : posicionesRayos) {
            Render.batch.draw(
                    texturaRayo,
                    x - ANCHO_RAYO / 2f,
                    sueloY,
                    ANCHO_RAYO,
                    altoMapa - sueloY);
        }
    }

    private final class Meteorito {

        private static final float TAMANO_METEORITO = 82f;
        private static final float ANCHO_FUEGO = 135f;
        private static final float ALTO_FUEGO = 48f;
        private static final float VELOCIDAD_ROTACION = 180f;
        private static final float DANIO_METEORITO = 1f;

        private final float x;
        private final Sprite sprite;
        private final Rectangle bounds = new Rectangle();
        private final int sentidoRotacion = random.nextBoolean() ? 1 : -1;
        private float y;
        private float velocidadY;
        private float tiempoFuego;
        private boolean impacto;

        private Meteorito(float x, float y) {
            this.x = x;
            this.y = y;
            sprite = new Sprite(texturaMeteorito);
            sprite.setSize(TAMANO_METEORITO, TAMANO_METEORITO);
            sprite.setOriginCenter();
            sprite.setPosition(x - TAMANO_METEORITO / 2f, y);
            bounds.set(x - TAMANO_METEORITO / 2f, y, TAMANO_METEORITO, TAMANO_METEORITO);
        }

        private boolean update(float delta) {
            if (!impacto) {
                velocidadY -= GRAVEDAD_METEORITO * delta;
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
                    jugador.restarVida(DANIO_METEORITO);
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

        private void dibujar() {
            sprite.draw(Render.batch);
        }
    }
}
