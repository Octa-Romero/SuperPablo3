package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.prz.juego.entidades.Jugador;
import com.prz.juego.entidades.Personajes;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.niveles.Nivel1;
import com.prz.juego.principal.Principal;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Musica;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

    private Principal juego;
    private Jugador jugador;
    private Nivel nivel;
    private Entrada entrada;
    private MenuPausa menuPausa;
    private boolean pausado = false;
    private Hud hud;

    public PantallaJuego(Principal juego, Personajes personajeElegido) {
        this.juego = juego;
        this.entrada = new Entrada();

        jugador = personajeElegido.crear(50, 150);
        jugador.setEntrada(entrada);

        nivel = new Nivel1(jugador);
        nivel.cargar();

        this.menuPausa = new MenuPausa(juego, this);

        Gdx.input.setInputProcessor(entrada);

        hud = new Hud(jugador, jugador.getTexturaHud(), Render.batch);
    }

    @Override
    public void show() {

        InputMultiplexer mux = new InputMultiplexer();

        mux.addProcessor(entrada);
        mux.addProcessor(menuPausa.getStage());

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
            nivel.update(delta, entrada);
        }

        nivel.renderMapa();

        Render.begin(nivel.getCamara());

        nivel.render();

        Render.end();

        nivel.renderDebug();

        hud.dibujar();

        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }

        if (nivel.isGameOver()) {

            Musica.parar();

            juego.setScreen(
                new GameOver(juego)
            );
        }
    }

    @Override
    public void resize(int width, int height) {

        nivel.resize(width, height);
        menuPausa.resize(width, height);
    }

    public void setPausa(boolean valor) {
        this.pausado = valor;
    }

    public void guardarPosicionCamara() {
        nivel.guardarPosicionCamara();
    }

    public void restaurarPosicionCamara() {
        nivel.restaurarPosicionCamara();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {

        nivel.dispose();
        menuPausa.dispose();
    }
}
