package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;

import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Personajes;
import com.prz.juego.niveles.Hub;
import com.prz.juego.principal.Navegable;
import com.prz.juego.principal.Principal;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;
import com.prz.juego.sistemas.Hud;

public class PantallaJuego implements Screen {

    private final Navegable nav;
    private Jugador jugador;
    private final Personajes personajeElegido;
    private final Nivel nivel;
    private final Entrada entrada;
    private final MenuPausa menuPausa;
    private boolean pausado = false;
    private final Hud hud;
    private Hub hub;


    public PantallaJuego(Navegable nav, Personajes personajeElegido) {
        this(nav, personajeElegido, null);
    }

    public PantallaJuego(Navegable nav, Personajes personajeElegido,String rutaNivel) {
        this.nav = nav;
        this.personajeElegido = personajeElegido;

        this.entrada = new Entrada();
        this.nivel = new Nivel();
        this.menuPausa = new MenuPausa(nav, this);

        jugador = personajeElegido.crear(50, 150);
        jugador.setEntrada(entrada);

        if (rutaNivel == null) {
            hub = new Hub();
            hub.cargar("Niveles/Niveles/Hub.tmx", jugador);
        } else {
            hub = null;
            nivel.cargar(rutaNivel, jugador);
        }

        hud = new Hud(jugador, Render.batch);
    }

    @Override
    public void show() {
        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(menuPausa.getStage());
        mux.addProcessor(entrada);

        Gdx.input.setInputProcessor(mux);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        if (entrada.aprietaEscape()) {
            pausado = !pausado;

            if (pausado) {
                menuPausa.activar();
            } else {
                menuPausa.desactivar();
            }
        }

        if (!pausado) {
            if (hub != null) {
                hub.update(delta, entrada);
            } else {
                nivel.update(delta, entrada);



            }
        }

        // Ir a la selección de niveles desde el Hub
        if (!pausado && hub != null
            && hub.estaEnCentroHub()
            && entrada.aprietaE()) {

            nav.cambiarPantalla(
                new SeleccionNivel(nav, personajeElegido)
            );
            return;
        }

        // Dibujar el Hub o el nivel seleccionado
        if (hub != null) {
            hub.renderMapa();

            Render.begin(hub.getCamara());
            hub.render();
            hub.dibujarAlertaCentro();
            Render.end();

        } else {
            nivel.renderMapa();

            Render.begin(nivel.getCamara());
            nivel.render();
            Render.end();

            nivel.renderDebug();

            if (nivel.isGameOver()) {
                nav.cambiarPantalla(new GameOver(nav));
                return;
            }
        }

        hud.dibujar();

        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (hub != null) {
            hub.resize(width, height);
        } else {
            nivel.resize(width, height);
        }

        menuPausa.resize(width, height);
        hud.actualizarTamano(width, height);
    }

    public void setPausa(boolean valor)
    {
        this.pausado = valor;
    }

    public void guardarPosicionCamara() {
        nivel.guardarPosicionCamara();
    }

    public void restaurarPosicionCamara() {
        nivel.restaurarPosicionCamara();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (hub != null) {
            hub.dispose();
        } else {
            nivel.dispose();
        }
        menuPausa.dispose();
    }

}
