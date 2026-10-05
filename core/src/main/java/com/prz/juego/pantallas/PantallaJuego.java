package com.prz.juego.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
    private final Nivel nivel;
    private final Entrada entrada;
    private final MenuPausa menuPausa;
    private boolean pausado = false;
    private final Hud hud;
    private boolean hudActivo = true;
    private Hub hub;
    private BitmapFont fontAlerta;

    public PantallaJuego(Navegable nav, Personajes personajeElegido) {
        this.nav = nav;
        this.entrada = new Entrada();
        this.nivel = new Nivel();
        this.menuPausa = new MenuPausa(nav, this);

        jugador = personajeElegido.crear(50, 150);

        jugador.setEntrada(entrada);
        hub = new Hub();
        hub.cargar("Niveles/Niveles/Hub.tmx", jugador);
        //nivel.cargar("Niveles/Niveles/nivel4.tmx", jugador);

        hud = new Hud(jugador, Render.batch);
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("assets/Fuentes/alagard.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parametro =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 28;
        parametro.color = Color.WHITE;
        parametro.shadowOffsetX = 2;
        parametro.shadowOffsetY = 2;
        parametro.shadowColor = new Color(0, 0, 0, 0.9f);

        fontAlerta = generator.generateFont(parametro);

        generator.dispose();
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
            hub.update(delta, entrada);
        }
        if(hub.estaEnCentroHub() && entrada.aprietaE()){
            nav.cambiarPantalla(new SeleccionNivel(nav));
            return;
        }

        hub.renderMapa();

        Render.begin(hub.getCamara());
        hub.render();
        Render.end();

        Render.begin(hub.getCamara());
        dibujarAlertaCentro();
        Render.end();


        hud.dibujar();


        if (pausado) {
            menuPausa.update(delta);
            menuPausa.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        hub.resize(width, height);
      //  nivel.resize(width, height);
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
        hub.dispose();
        //nivel.dispose();
        menuPausa.dispose();
        fontAlerta.dispose();
    }

    private void dibujarAlertaCentro() {

        if (!hub.estaEnCentroHub()) {
            return;
        }

        String mensaje = "PRESIONA E PARA SELECCIONAR NIVEL";

        GlyphLayout layout = new GlyphLayout(fontAlerta, mensaje);

        float x = (hub.getCamara().viewportWidth - layout.width) / 2f;
        float y = 80;

        fontAlerta.draw(Render.batch, mensaje, x, y);
    }
}
