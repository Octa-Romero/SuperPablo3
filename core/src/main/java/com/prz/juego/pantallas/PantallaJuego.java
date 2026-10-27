package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.entidades.jugadores.Personajes;
import com.prz.juego.niveles.Hub;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.principal.Navegable;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

    private final Navegable NAV;
    private Jugador jugador;
    private final Personajes PERSONAJE_ELEGIDO;
    private final Nivel NIVEL;
    private final Entrada ENTRADA;
    private final MenuPausa MENU_PAUSA;
    private boolean pausado = false;
    private final Hud HUD;
    private Hub hub;

    public PantallaJuego(Navegable NAV, Personajes PERSONAJE_ELEGIDO) {
        this(NAV, PERSONAJE_ELEGIDO, null);
    }

    public PantallaJuego(Navegable NAV, Personajes PERSONAJE_ELEGIDO, String rutaNivel) {
        this.NAV = NAV;
        this.PERSONAJE_ELEGIDO = PERSONAJE_ELEGIDO;

        this.ENTRADA = new Entrada();
        this.NIVEL = rutaNivel == null ? null : Nivel.crear(rutaNivel);
        this.MENU_PAUSA = new MenuPausa(NAV, this);

        jugador = PERSONAJE_ELEGIDO.crear(50, 150);
        jugador.setEntrada(ENTRADA);

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar("Niveles/Niveles/Hub.tmx", jugador);
        } else {
            hub = null;
            NIVEL.cargar(jugador);
        }

        HUD = new Hud(jugador, Render.batch);
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(!pausado);

        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(MENU_PAUSA.getStage());
        mux.addProcessor(ENTRADA);

        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        if (ENTRADA.aprietaEscape()) {
            setPausa(!pausado);

            if (pausado) {
                MENU_PAUSA.activar();
            } else {
                MENU_PAUSA.desactivar();
            }
        }

        if (!pausado) {
            if (hub != null) {
                hub.update(delta, ENTRADA);
            } else {
                NIVEL.update(delta, ENTRADA);
            }

            limitarJugadorDentroDePantalla();
        }

        // Ir a la seleccion de niveles desde el Hub
        if (!pausado && hub != null && hub.estaEnCentroHub() && ENTRADA.aprietaE()) {

            guardarPosicionCamara();
            ENTRADA.limpiar();
            NAV.cambiarPantalla(new SeleccionNivel(NAV, PERSONAJE_ELEGIDO, this));
            return;
        }

        // Dibujar el Hub o el NIVEL seleccionado
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro();
            Render.end();

        } else {
            NIVEL.renderMapa();

            Render.begin(NIVEL.getCamara());
            NIVEL.render();
            Render.end();

            NIVEL.renderDebug();

            if (NIVEL.isGameOver()) {
                NAV.cambiarPantalla(new GameOver(NAV));
                return;
            }
        }

        if (hub == null) {
            HUD.dibujar();
        }

        if (pausado) {
            MENU_PAUSA.update(delta);
            MENU_PAUSA.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (hub != null) {
            hub.resize(width, height);
        } else {
            NIVEL.resize(width, height);
        }

        MENU_PAUSA.resize(width, height);
        HUD.actualizarTamano(width, height);
    }

    public void setPausa(boolean valor) {
        this.pausado = valor;
        Gdx.input.setCursorCatched(!valor);
    }

    private void limitarJugadorDentroDePantalla() {
        Camera CAMARA_ACTUAL = hub != null ? hub.getCamara() : NIVEL.getCamara();
        float CENTRO_Y = Gdx.graphics.getHeight() / 2f;
        float BORDE_IZQUIERDO = CAMARA_ACTUAL.unproject(new Vector3(0, CENTRO_Y, 0)).x;
        float BORDE_DERECHO =
                CAMARA_ACTUAL.unproject(new Vector3(Gdx.graphics.getWidth(), CENTRO_Y, 0)).x;

        jugador.limitarMovimientoHorizontal(BORDE_IZQUIERDO, BORDE_DERECHO);
    }

    public void guardarPosicionCamara() {
        if (hub != null) {
            hub.guardarPosicionCamara();
        } else {
            NIVEL.guardarPosicionCamara();
        }
    }

    public void restaurarPosicionCamara() {
        if (hub != null) {
            hub.restaurarPosicionCamara();
        } else {
            NIVEL.restaurarPosicionCamara();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void dispose() {
        if (hub != null) {
            hub.dispose();
        } else {
            NIVEL.dispose();
        }
        MENU_PAUSA.dispose();
    }
}
