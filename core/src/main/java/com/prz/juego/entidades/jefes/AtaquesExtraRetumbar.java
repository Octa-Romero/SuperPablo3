package com.prz.juego.entidades.jefes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    private static final float DURACION_LLUVIA_FUEGUITOS = 15f;
    private static final float TIEMPO_DESPEJE_FUEGUITOS = 2f;
    private static final float INTERVALO_FUEGUITOS = 0.1f;
    private static final float DURACION_AVISO_RAYO = 1.5f;
    private static final float INTERVALO_PARPADEO_RAYO = 0.15f;
    private static final float DURACION_RAYO = 0.3f;
    private static final float PAUSA_ENTRE_OLEADAS = 0.6f;
    private static final int CANTIDAD_METEORITOS = 3;
    private static final int CANTIDAD_RAYOS = 5;
    private static final int CANTIDAD_OLEADAS = 3;
    private static final float ANCHO_RAYO = 95f;
    private static final float DANIO = 1;

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
    private final Consumer<Entidad> eliminarEntidad;
    private final Random random = new Random();
    private final List<Meteorito> meteoritos = new ArrayList<>();
    private final List<Fueguito> fueguitos = new ArrayList<>();
    private final List<BichoRango> bichosInvocados = new ArrayList<>();
    private final Texture texturaMeteorito;
    private final Texture texturaRayo;
    private final Texture texturaPixel;
    private final Texture texturaFueguito;
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
    private boolean lluviaFueguitosActiva;
    private float tiempoLluviaFueguitos;
    private float tiempoSiguienteFueguito;
    private final float[] posicionesRayos = new float[CANTIDAD_RAYOS];

    AtaquesExtraRetumbar(
            Jugador jugador,
            float anchoMapa,
            float altoMapa,
            float sueloY,
            Consumer<Entidad> agregarEntidad,
            Consumer<Entidad> eliminarEntidad) {
        this.jugador = jugador;
        this.anchoMapa = anchoMapa;
        this.altoMapa = altoMapa;
        this.sueloY = sueloY;
        this.agregarEntidad = agregarEntidad;
        this.eliminarEntidad = eliminarEntidad;

        texturaMeteorito = GestorRecursos.obtenerTextura("Jefe/meteorito.png");
        Texture texturaFuegoMeteorito = GestorRecursos.obtenerTextura("Jefe/fuegoMeteorito.png");
        texturaRayo = GestorRecursos.obtenerTextura("Jefe/rayo.png");
        texturaPixel = GestorRecursos.obtenerPixel();
        texturaFueguito = GestorRecursos.obtenerTextura("Jefe/fueguito.png");

        TextureRegion[][] frames = TextureRegion.split(texturaFuegoMeteorito, 1024, 342);
        animacionFuego =
                new Animation<>(0.14f, frames[0][0], frames[0][1], frames[0][2]);
        animacionFuego.setPlayMode(Animation.PlayMode.LOOP);
    }

    void update(float delta) {
        if (lluviaFueguitosActiva) {
            actualizarLluviaFueguitos(delta);
            return;
        }

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
        meteoritos.add(
            new Meteorito(
                x,
                altoMapa + 30f,
                sueloY,
                jugador,
                texturaMeteorito,
                animacionFuego));
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
        bichosInvocados.add(izquierdo);
        bichosInvocados.add(derecho);
        agregarEntidad.accept(izquierdo);
        agregarEntidad.accept(derecho);
    }

    void cancelarAtaques() {
        ataqueActual = TipoAtaque.NINGUNO;
        tiempoCooldown = 0f;
        meteoritos.clear();
        fueguitos.clear();
        lluviaFueguitosActiva = false;
        faseRayos = null;
        mostrarAvisoRayo = false;

        for (BichoRango bicho : bichosInvocados) {
            eliminarEntidad.accept(bicho);
        }
        bichosInvocados.clear();
    }

    void iniciarLluviaFueguitos() {
        lluviaFueguitosActiva = true;
        tiempoLluviaFueguitos = DURACION_LLUVIA_FUEGUITOS + TIEMPO_DESPEJE_FUEGUITOS;
        tiempoSiguienteFueguito = 0f;
        fueguitos.clear();
    }

    boolean estaLluviaFueguitosActiva() {
        return lluviaFueguitosActiva;
    }

    private void actualizarLluviaFueguitos(float delta) {
        tiempoLluviaFueguitos = Math.max(0f, tiempoLluviaFueguitos - delta);
        tiempoSiguienteFueguito -= delta;

        while (tiempoLluviaFueguitos > TIEMPO_DESPEJE_FUEGUITOS
                && tiempoSiguienteFueguito <= 0f) {
            invocarFueguito();
            tiempoSiguienteFueguito += INTERVALO_FUEGUITOS;
        }

        fueguitos.removeIf(fueguito -> !fueguito.update(delta));
        if (tiempoLluviaFueguitos <= 0f) {
            lluviaFueguitosActiva = false;
            fueguitos.clear();
        }
    }

    private void invocarFueguito() {
        float x = random.nextFloat() * Math.max(0f, anchoMapa);
        float y = altoMapa + random.nextFloat() * 250f;
        float tamano = 28f + random.nextFloat() * 24f;
        float velocidad = 300f + random.nextFloat() * 250f;
        fueguitos.add(
            new Fueguito(x, y, tamano, velocidad, sueloY, jugador, texturaFueguito));
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
        for (float x : posicionesRayos) {
            Rectangle boundsRayo =
                    new Rectangle(x - ANCHO_RAYO / 2f, sueloY, ANCHO_RAYO, alturaRayo);
            if (boundsJugador.overlaps(boundsRayo)) {
                jugador.restarVida(DANIO);
            }
        }
        Sonido.RELAMPAGO.sonar();
    }

    private void terminarAtaque() {
        ataqueActual = TipoAtaque.NINGUNO;
        tiempoCooldown = 0f;
    }

    private void actualizarMeteoritos(float delta) {
        meteoritos.removeIf(meteorito -> !meteorito.update(delta));
    }

    void dibujar() {
        if (lluviaFueguitosActiva) {
            for (Fueguito fueguito : fueguitos) {
                fueguito.dibujar();
            }
            return;
        }

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
        if (lluviaFueguitosActiva) {
            for (Fueguito fueguito : fueguitos) {
                shapeRenderer.circle(
                    fueguito.getHitbox().x,
                    fueguito.getHitbox().y,
                    fueguito.getHitbox().radius);
            }
            return;
        }

        for (Meteorito meteorito : meteoritos) {
            shapeRenderer.rect(
                    meteorito.getBounds().x,
                    meteorito.getBounds().y,
                    meteorito.getBounds().width,
                    meteorito.getBounds().height);
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

}
