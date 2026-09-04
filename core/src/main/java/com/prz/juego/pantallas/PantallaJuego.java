package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.prz.juego.Principal;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;


public class PantallaJuego implements Screen {

    private Render render;
    private Nivel nivel;
    private Entrada entrada;
    private MenuPausa menuPausa;
    private boolean pausado = false;

    public PantallaJuego(Principal juego) {
        this.render = juego.getRender();
        this.entrada = new Entrada();
        this.nivel = new Nivel();
        this.menuPausa = new MenuPausa(juego, this);
        nivel.cargar("Niveless/Niveles/Level1.tmx");
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

        render.limpiarPantalla();

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

        render.begin(nivel.getCamara());
        nivel.render(render.getBatch());
        render.end();

        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        nivel.resize(width, height);
        menuPausa.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        nivel.dispose();
        menuPausa.dispose();
    }

    public void setPausa(boolean valor)
    {
        this.pausado = valor;
    }
}
