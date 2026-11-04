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

class AtaquesExtraRetumbar {

    private static final float COOLDOWN = 10f;
    private static final float INTERVALO_METEORITOS = 0.5f;
    private static final float DURACION_LLUVIA_FUEGUITOS = 15f;
    private static final float TIEMPO_DESPEJE_FUEGUITOS = 2f;
    private static final float INTERVALO_FUEGUITOS = 0.08f;
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

    private final Jugador JUGADOR;
    private final float ANCHO_MAPA;
    private final float ALTO_MAPA;
    private final float SUELO_Y;
    private final Consumer<Entidad> AGREGAR_ENTIDAD;
    private final Consumer<Entidad> ELIMINAR_ENTIDAD;
    private final Random RANDOM = new Random();
    private final List<Meteorito> METEORITOS = new ArrayList<>();
    private final List<Fueguito> FUEGUITOS = new ArrayList<>();
    private final List<BichoRango> BICHOS_INVOCADOS = new ArrayList<>();
    private final Texture TEXTURA_METEORITO;
    private final Texture TEXTURA_RAYO;
    private final Texture TEXTURA_PIXEL;
    private final Texture TEXTURA_FUEGUITO;
    private final Animation<TextureRegion> ANIMACION_FUEGO;

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
    private final float[] POSICIONES_RAYOS = new float[CANTIDAD_RAYOS];

    AtaquesExtraRetumbar(
            Jugador jugador,
            float anchoMapa,
            float altoMapa,
            float sueloY,
            Consumer<Entidad> agregarEntidad,
            Consumer<Entidad> eliminarEntidad) {
        this.JUGADOR = jugador;
        this.ANCHO_MAPA = anchoMapa;
        this.ALTO_MAPA = altoMapa;
        this.SUELO_Y = sueloY;
        this.AGREGAR_ENTIDAD = agregarEntidad;
        this.ELIMINAR_ENTIDAD = eliminarEntidad;

        TEXTURA_METEORITO = GestorRecursos.obtenerTextura("Jefe/meteorito.png");
        Texture texturaFuegoMeteorito = GestorRecursos.obtenerTextura("Jefe/fuegoMeteorito.png");
        TEXTURA_RAYO = GestorRecursos.obtenerTextura("Jefe/rayo.png");
        TEXTURA_PIXEL = GestorRecursos.obtenerPixel();
        TEXTURA_FUEGUITO = GestorRecursos.obtenerTextura("Jefe/fueguito.png");

        TextureRegion[][] frames = TextureRegion.split(texturaFuegoMeteorito, 1024, 342);
        ANIMACION_FUEGO =
                new Animation<>(0.14f, frames[0][0], frames[0][1], frames[0][2]);
        ANIMACION_FUEGO.setPlayMode(Animation.PlayMode.LOOP);
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
        TipoAtaque[] ataques = TipoAtaque.values();
        ataqueActual = ataques[RANDOM.nextInt(ataques.length)];
        tiempoCooldown = 0f;

        switch (ataqueActual) {
            case NINGUNO:
                break;
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
        float x = margen + RANDOM.nextFloat() * Math.max(0f, ANCHO_MAPA - 2f * margen);
        METEORITOS.add(
            new Meteorito(
                x,
                ALTO_MAPA + 30f,
                SUELO_Y,
                JUGADOR,
                TEXTURA_METEORITO,
                ANIMACION_FUEGO));
    }

    private void invocarBichos() {
        float anchoBicho = 70f;
        BichoRango izquierdo =
                new BichoRango(50f, ALTO_MAPA + 30f, JUGADOR, EnemigoRango.Modo.MOVIL);
        BichoRango derecho =
                new BichoRango(
                        Math.max(50f, ANCHO_MAPA - 50f - anchoBicho),
                        ALTO_MAPA + 30f,
                        JUGADOR,
                        EnemigoRango.Modo.MOVIL);
        izquierdo.configurarRetrasoInicialAntesDeAtacar(1f);
        derecho.configurarRetrasoInicialAntesDeAtacar(1f);
        derecho.configurarDireccionInicial(-1f);
        BICHOS_INVOCADOS.add(izquierdo);
        BICHOS_INVOCADOS.add(derecho);
        AGREGAR_ENTIDAD.accept(izquierdo);
        AGREGAR_ENTIDAD.accept(derecho);
    }

    void cancelarAtaques() {
        ataqueActual = TipoAtaque.NINGUNO;
        tiempoCooldown = 0f;
        METEORITOS.clear();
        FUEGUITOS.clear();
        lluviaFueguitosActiva = false;
        faseRayos = null;
        mostrarAvisoRayo = false;

        for (BichoRango bicho : BICHOS_INVOCADOS) {
            ELIMINAR_ENTIDAD.accept(bicho);
        }
        BICHOS_INVOCADOS.clear();
    }

    void iniciarLluviaFueguitos() {
        lluviaFueguitosActiva = true;
        tiempoLluviaFueguitos = DURACION_LLUVIA_FUEGUITOS + TIEMPO_DESPEJE_FUEGUITOS;
        tiempoSiguienteFueguito = 0f;
        FUEGUITOS.clear();
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

        FUEGUITOS.removeIf(fueguito -> !fueguito.update(delta));
        if (tiempoLluviaFueguitos <= 0f) {
            lluviaFueguitosActiva = false;
            FUEGUITOS.clear();
        }
    }

    private void invocarFueguito() {
        float x = RANDOM.nextFloat() * Math.max(0f, ANCHO_MAPA);
        float y = ALTO_MAPA + RANDOM.nextFloat() * 250f;
        float tamano = 28f + RANDOM.nextFloat() * 24f;
        float velocidad = 300f + RANDOM.nextFloat() * 250f;
        FUEGUITOS.add(
            new Fueguito(x, y, tamano, velocidad, SUELO_Y, JUGADOR, TEXTURA_FUEGUITO));
    }

    private void iniciarOleadaRayos() {
        for (int i = 0; i < POSICIONES_RAYOS.length; i++) {
            float margen = ANCHO_RAYO / 2f;
            POSICIONES_RAYOS[i] =
                    margen + RANDOM.nextFloat() * Math.max(0f, ANCHO_MAPA - 2f * margen);
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
        Rectangle boundsJugador = JUGADOR.getBounds();
        float alturaRayo = ALTO_MAPA - SUELO_Y;
        for (float x : POSICIONES_RAYOS) {
            Rectangle boundsRayo =
                    new Rectangle(x - ANCHO_RAYO / 2f, SUELO_Y, ANCHO_RAYO, alturaRayo);
            if (boundsJugador.overlaps(boundsRayo)) {
                JUGADOR.restarVida(DANIO);
            }
        }
        Sonido.RELAMPAGO.sonar();
    }

    private void terminarAtaque() {
        ataqueActual = TipoAtaque.NINGUNO;
        tiempoCooldown = 0f;
    }

    private void actualizarMeteoritos(float delta) {
        METEORITOS.removeIf(meteorito -> !meteorito.update(delta));
    }

    void dibujar() {
        if (lluviaFueguitosActiva) {
            for (Fueguito fueguito : FUEGUITOS) {
                fueguito.dibujar();
            }
            return;
        }

        for (Meteorito meteorito : METEORITOS) {
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
            for (Fueguito fueguito : FUEGUITOS) {
                shapeRenderer.circle(
                    fueguito.getHitbox().x,
                    fueguito.getHitbox().y,
                    fueguito.getHitbox().radius);
            }
            return;
        }

        for (Meteorito meteorito : METEORITOS) {
            shapeRenderer.rect(
                    meteorito.getBounds().x,
                    meteorito.getBounds().y,
                    meteorito.getBounds().width,
                    meteorito.getBounds().height);
        }

        if (ataqueActual == TipoAtaque.RAYOS && faseRayos == FaseRayos.IMPACTO) {
            float alturaRayo = ALTO_MAPA - SUELO_Y;
            for (float x : POSICIONES_RAYOS) {
                shapeRenderer.rect(
                        x - ANCHO_RAYO / 2f, SUELO_Y, ANCHO_RAYO, alturaRayo);
            }
        }
    }

    private void dibujarAvisosRayos() {
        if (!mostrarAvisoRayo) {
            return;
        }

        Color colorAnterior = Render.batch.getColor().cpy();
        Render.batch.setColor(1f, 0f, 0f, 0.35f);
        for (float x : POSICIONES_RAYOS) {
            Render.batch.draw(
                    TEXTURA_PIXEL,
                    x - ANCHO_RAYO / 2f,
                    SUELO_Y,
                    ANCHO_RAYO,
                    ALTO_MAPA - SUELO_Y);
        }
        Render.batch.setColor(colorAnterior);
    }

    private void dibujarRayos() {
        for (float x : POSICIONES_RAYOS) {
            Render.batch.draw(
                    TEXTURA_RAYO,
                    x - ANCHO_RAYO / 2f,
                    SUELO_Y,
                    ANCHO_RAYO,
                    ALTO_MAPA - SUELO_Y);
        }
    }

}
