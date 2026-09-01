package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.prz.juego.Principal;
import com.prz.juego.niveles.Nivel;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public class PantallaJuego implements Screen {

    private Principal juego;
    private Render render;
    private Nivel nivel;
    private Entrada entrada;

    public PantallaJuego(Principal juego) {
        this.juego = juego;
        this.render = juego.getRender();
        this.entrada = new Entrada();
        this.nivel = new Nivel();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(entrada);
        nivel.cargar("Niveless/Niveles/Level1.tmx");
    }

    @Override
    public void render(float delta) {
        render.limpiarPantalla();

        if (entrada.aprietaEscape()) {
            juego.setScreen(new PantallaPausa(juego));
            return;
        }
        nivel.update(delta, entrada);
        render.begin(nivel.getCamara());
        nivel.render(render.getBatch());
        render.end();
    }

    @Override
    public void resize(int width, int height) {
        nivel.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        nivel.dispose();
    }
}
